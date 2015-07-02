package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import name.falgout.jeffrey.concurrent.CompletableFutures;
import name.falgout.jeffrey.stream.future.FutureBaseStream;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingBiFunction;
import throwing.function.ThrowingFunction;
import throwing.function.ThrowingSupplier;
import throwing.stream.adapter.AbstractAdapter;
import throwing.stream.intermediate.adapter.ThrowingBaseStreamIntermediateAdapter;
import throwing.stream.intermediate.adapter.ThrowingFunctionAdapter;
import throwing.stream.union.UnionBaseStream;

abstract class FutureBaseStreamAdapter<T, D extends UnionBaseStream<T, FutureThrowable, D>, S extends FutureBaseStream<T, S>> extends
    AbstractAdapter<D> implements
    FutureBaseStream<T, S>,
    ThrowingBaseStreamIntermediateAdapter<D, S> {
  private final ThrowingFunctionAdapter<ExecutionException, Throwable> adapter;
  private final Complete complete;

  FutureBaseStreamAdapter(D delegate) {
    super(delegate);
    adapter = getDefaultAdapter();
    complete = CompletableFutures::newCompletableFuture;
  }

  FutureBaseStreamAdapter(D delegate, Executor executor) {
    super(delegate);
    adapter = getDefaultAdapter();
    complete = new AsyncComplete(executor);
  }

  private ThrowingFunctionAdapter<ExecutionException, Throwable> getDefaultAdapter() {
    return ThrowingFunctionAdapter.rethrow(ExecutionException.class, Throwable.class,
        ExecutionException::new);
  }

  FutureBaseStreamAdapter(D delegate, FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate);
    adapter = parent.adapter;
    this.complete = parent.complete;
  }

  public ThrowingFunctionAdapter<ExecutionException, Throwable> getFunctionAdapter() {
    return adapter;
  }

  @Override
  public void close() {
    getDelegate().close();
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
