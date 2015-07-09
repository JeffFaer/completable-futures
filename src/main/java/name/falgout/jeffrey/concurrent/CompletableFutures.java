package name.falgout.jeffrey.concurrent;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import name.falgout.jeffrey.stream.future.FutureStream;
import name.falgout.jeffrey.stream.future.adapter.FutureStreamBridge;
import throwing.function.ThrowingBiFunction;
import throwing.function.ThrowingSupplier;

public final class CompletableFutures {
  private CompletableFutures() {}

  /**
   * <p>
   * Creates a new {@code CompletableFuture} which will be completed by the given
   * {@code ThrowingSupplier}. If the supplier throws an exception, the {@code CompletableFuture}
   * will be {@link CompletableFuture#completeExceptionally(Throwable) completed exceptionally}.
   *
   * <p>
   * {@link ThrowingSupplier#get()} will be called from the
   * {@link CompletableFuture#runAsync(Runnable) default executor}.
   *
   * @param supplier
   *          a supplier returning the value for the {@code CompletableFuture}.
   * @return the new {@code CompletableFuture}
   */
  public static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier) {
    return newCompletableFuture(supplier,
        (Function<Runnable, CompletableFuture<Void>>) CompletableFuture::runAsync);
  }

  /**
   * <p>
   * Creates a new {@code CompletableFuture} which will be completed by the given
   * {@code ThrowingSupplier}. If the supplier throws an exception, the {@code CompletableFuture}
   * will be {@link CompletableFuture#completeExceptionally(Throwable) completed exceptionally}. *
   *
   * <p>
   * {@link ThrowingSupplier#get()} will be called from the
   * {@link CompletableFuture#runAsync(Runnable, Executor) given executor}.
   *
   * @param supplier
   *          a supplier returning the value for the {@code CompletableFuture}.
   * @param executor
   *          the executor to call {@link ThrowingSupplier#get() get} from.
   * @return the new {@code CompletableFuture}
   */
  public static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier,
      Executor executor) {
    return newCompletableFuture(supplier,
        (Function<Runnable, CompletableFuture<Void>>) r -> CompletableFuture.runAsync(r, executor));
  }

  private static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier,
      Function<Runnable, CompletableFuture<Void>> executor) {
    CompletableFuture<T> future = new CompletableFuture<>();
    executor.apply(() -> {
      try {
        future.complete(supplier.get());
      } catch (Throwable t) {
        future.completeExceptionally(t);
      }
    });
    return future;
  }

  /**
   * Returns a supplier version of calling {@link Future#get() get} on a {@code Future}. If
   * {@code get} throws an {@code ExecutionException}, it is unwrapped and its
   * {@link ExecutionException#getCause() cause} is thrown instead.
   *
   * @param future
   *          a {@code Future}
   * @return a supplier version of {@code Future.get()}
   */
  public static <T> ThrowingSupplier<T, ?> get(Future<T> future) {
    return get(future::get);
  }

  public static <T> ThrowingBiFunction<Long, TimeUnit, T, ?> getTimed(Future<T> future) {
    return (timeout, unit) -> getTimed(future, timeout, unit).get();
  }

  public static <T> ThrowingSupplier<T, ?> getTimed(Future<T> future, long timeout, TimeUnit unit) {
    return get(() -> future.get(timeout, unit));
  }

  private static <T> ThrowingSupplier<T, ?> get(ThrowingSupplier<T, ?> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (Throwable t) {
        if (t instanceof ExecutionException) {
          t = t.getCause();
        }

        throw t;
      }
    };
  }

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future) {
    return future instanceof CompletableFuture ? (CompletableFuture<T>) future
        : newCompletableFuture(get(future));
  }

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future, Executor executor) {
    return future instanceof CompletableFuture ? (CompletableFuture<T>) future
        : newCompletableFuture(get(future), executor);
  }

  /**
   * Creates a new immediately failed {@code CompletableFuture}.
   *
   * @param cause
   *          the exception to rethrow
   * @return a new {@link CompletableFuture#isCompletedExceptionally() exceptionally completed}
   *         {@code CompletableFuture}.
   */
  public static <T> CompletableFuture<T> failed(Throwable cause) {
    CompletableFuture<T> f = new CompletableFuture<>();
    f.completeExceptionally(cause);
    return f;
  }

  @SafeVarargs
  public static <T> Future<List<T>> allOf(Future<T>... futures) {
    return allOf(Arrays.asList(futures));
  }

  public static <T> Future<List<T>> allOf(Iterable<? extends Future<T>> futures) {
    CompletableFuture<?>[] futuresArray = toArray(futures);
    CompletableFuture<Void> f = CompletableFuture.allOf(futuresArray);

    CompletableFuture<List<T>> all = f.thenApply(nil -> Arrays.stream(futuresArray)
        .map(CompletableFuture::join)
        .map(CompletableFutures.<T> unsafeCast())
        .collect(toList()));

    return all;
  }

  private static <T> CompletableFuture<?>[] toArray(Iterable<? extends Future<T>> futures) {
    return StreamSupport.stream(futures.spliterator(), false)
        .map(CompletableFutures::newCompletableFuture)
        .toArray(CompletableFuture<?>[]::new);
  }

  @SuppressWarnings("unchecked")
  private static <T> Function<Object, T> unsafeCast() {
    return o -> (T) o;
  }

  @SafeVarargs
  public static <T> Future<Optional<T>> anyOf(Future<T>... rest) {
    return anyOf(Arrays.asList(rest));
  }

  @SafeVarargs
  public static <T> Future<T> anyOf(Future<T> first, Future<T>... rest) {
    List<Future<T>> l = new ArrayList<>(1 + rest.length);
    l.add(first);
    l.addAll(Arrays.asList(rest));
    CompletableFuture<Optional<T>> f = newCompletableFuture(anyOf(l));

    return f.thenApply(Optional::get);
  }

  /**
   * Returns a new {@code Future} that is completed when any of the given {@code Future}s complete,
   * with the same result. Otherwise, if it completed exceptionally, the returned {@code Future}
   * also does so, with a CompletionException holding this exception as its cause. If no
   * {@code Future}s are provided, returns a {@code Future} which contains {@code Optional.empty()}.
   *
   * @param futures
   *          the futures
   * @return a new {@code Future} that is completed with the result or exception of any of the given
   *         {@code Future}s when one completes
   */
  public static <T> Future<Optional<T>> anyOf(Iterable<? extends Future<T>> futures) {
    CompletableFuture<?>[] futuresArray = toArray(futures);
    if (futuresArray.length == 0) {
      return CompletableFuture.completedFuture(Optional.empty());
    }

    CompletableFuture<Object> f = CompletableFuture.anyOf(futuresArray);
    return f.thenApply(CompletableFutures.<T> unsafeCast()).thenApply(Optional::of);
  }

  @SafeVarargs
  public static <T> FutureStream<T> stream(Future<T>... futures) {
    return stream(Arrays.asList(futures));
  }

  public static <T> FutureStream<T> stream(Iterable<? extends Future<T>> futures) {
    return FutureStreamBridge.of(futures.spliterator());
  }

  @SafeVarargs
  public static <T> FutureStream<T> stream(Executor executor, Future<T>... futures) {
    return stream(executor, Arrays.asList(futures));
  }

  public static <T> FutureStream<T> stream(Executor executor, Iterable<? extends Future<T>> futures) {
    return FutureStreamBridge.of(futures.spliterator(), executor);
  }
}
