package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import name.falgout.jeffrey.concurrent.CompletableFutures;
import name.falgout.jeffrey.stream.future.FutureBaseStream;
import name.falgout.jeffrey.stream.future.FutureIterator;
import name.falgout.jeffrey.stream.future.FutureSpliterator;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingBiFunction;
import throwing.function.ThrowingFunction;
import throwing.function.ThrowingSupplier;
import throwing.stream.ThrowingBaseStream;
import throwing.stream.adapter.AbstractAdapter;
import throwing.stream.adapter.ChainingAdapter;
import throwing.stream.union.UnionBaseStream;

abstract class FutureBaseStreamAdapter<T, D extends UnionBaseStream<T, FutureThrowable, D, ?>, S extends FutureBaseStream<T, S>> extends
    AbstractAdapter<D> implements FutureBaseStream<T, S>, ChainingAdapter<D, S> {
  private final Complete complete;

  FutureBaseStreamAdapter(D delegate) {
    super(delegate);
    complete = CompletableFutures::newCompletableFuture;
  }

  FutureBaseStreamAdapter(D delegate, Executor executor) {
    super(delegate);
    complete = new AsyncComplete(executor);
  }

  FutureBaseStreamAdapter(D delegate, FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate);
    this.complete = parent.complete;
  }

  @Override
  public boolean isParallel() {
    return getDelegate().isParallel();
  }

  @Override
  public S sequential() {
    return chain(UnionBaseStream::sequential);
  }

  @Override
  public S parallel() {
    return chain(UnionBaseStream::parallel);
  }

  @Override
  public S unordered() {
    return chain(UnionBaseStream::unordered);
  }

  @SuppressWarnings("unchecked")
  @Override
  public S onClose(Runnable closeHandler) {
    // Wow, this is a gross workaround.
    return chain((D) ((ThrowingBaseStream<T, Throwable, ?>) getDelegate()).onClose(closeHandler));
  }

  @Override
  public void close() {
    getDelegate().close();
  }

  @Override
  public FutureSpliterator<T> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public FutureIterator<T> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  protected <U> Future<Void> completeVoid(ThrowingBiConsumer<? super D, U, FutureThrowable> action,
      U secondArgument) {
    return complete(d -> {
      action.accept(d, secondArgument);
      return null;
    });
  }

  protected <R> Future<R> complete(ThrowingFunction<? super D, R, FutureThrowable> function) {
    return complete((d, nil) -> function.apply(d), null);
  }

  protected <U, R> Future<R> complete(
      ThrowingBiFunction<? super D, U, R, FutureThrowable> function, U secondArgument) {
    return complete.newCompletableFuture(() -> {
      try {
        return function.apply(getDelegate(), secondArgument);
      } catch (FutureThrowable e) {
        throw e.rethrow();
      }
    });
  }

  @FunctionalInterface
  private static interface Complete {
    public <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier);
  }

  private static class AsyncComplete implements Complete {
    private final Executor executor;

    public AsyncComplete(Executor executor) {
      this.executor = executor;
    }

    @Override
    public <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier) {
      return CompletableFutures.newCompletableFuture(supplier, executor);
    }
  }
}
