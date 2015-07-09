package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.StreamSupport;

import name.falgout.jeffrey.concurrent.CompletableFutures;
import throwing.ThrowingBaseSpliterator.ThrowingSpliterator;
import throwing.function.ThrowingConsumer;

class UnorderedFutureSpliterator<T> implements ThrowingSpliterator<T, FutureThrowable> {
  public static <T> UnorderedFutureSpliterator<T> create(
      java.util.Spliterator<? extends Future<T>> futures) {
    return create(futures, new LinkedBlockingQueue<>());
  }

  static <T> UnorderedFutureSpliterator<T> create(
      java.util.Spliterator<? extends Future<T>> futures, BlockingQueue<Future<T>> queue) {
    Lock lock = new ReentrantLock();
    Condition addedOrFailed = lock.newCondition();
    CompletableFuture<CompletableFuture<?>> futureAll = new CompletableFuture<>(); // gross

    CompletableFuture<?>[] completableFutures = StreamSupport.stream(futures, false)
        .map(CompletableFutures::newCompletableFuture)
        .map(cf -> cf.thenAccept(t -> {
          if (!futureAll.join().isDone()) {
            try {
              queue.put(cf);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              futureAll.join().completeExceptionally(e);
            }

            lock.lock();
            try {
              addedOrFailed.signalAll();
            } finally {
              lock.unlock();
            }
          }
        }))
        .toArray(CompletableFuture<?>[]::new);
    CompletableFuture<?> all = CompletableFuture.allOf(completableFutures);
    futureAll.complete(all);

    return new UnorderedFutureSpliterator<>(lock, addedOrFailed, queue, all,
        futures.characteristics(), futures.estimateSize());
  }

  private final Lock lock;
  private final Condition addedOrFailed;

  private final BlockingQueue<Future<T>> queue;
  private final CompletableFuture<?> all;

  private final int characteristics;
  private long estimatedSize;

  private UnorderedFutureSpliterator(Lock lock, Condition addedOrFailed,
      BlockingQueue<Future<T>> queue, CompletableFuture<?> all, int characteristics,
      long estimatedSize) {
    this.lock = lock;
    this.addedOrFailed = addedOrFailed;
    this.queue = queue;
    this.all = all;
    this.characteristics = characteristics;
    this.estimatedSize = estimatedSize;
  }

  @Override
  public boolean tryAdvance(ThrowingConsumer<? super T, ? extends FutureThrowable> action)
      throws FutureThrowable {
    try {
      while (true) {
        lock.lock();
        try {
          while (!all.isDone() && queue.isEmpty()) {
            addedOrFailed.await();
          }
        } finally {
          lock.unlock();
        }

        if (all.isDone() && queue.isEmpty()) {
          all.get(); // check for exception
          return false;
        } else {
          Future<T> f = queue.poll();
          if (f != null) {
            action.accept(f.get());

            return !(all.isDone() && queue.isEmpty());
          }
        }
      }
    } catch (InterruptedException | ExecutionException | CancellationException e) {
      throw new FutureThrowable(e);
    }
  }

  @Override
  public UnorderedFutureSpliterator<T> trySplit() {
    estimatedSize /= 2;
    return new UnorderedFutureSpliterator<>(lock, addedOrFailed, queue, all, characteristics,
        estimatedSize);
  }

  @Override
  public long estimateSize() {
    return estimatedSize;
  }

  @Override
  public int characteristics() {
    return characteristics;
  }
}
