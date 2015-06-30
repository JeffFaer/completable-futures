package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class CompletableFutureAdapter<D> {
  private final D delegate;
  private final ThenApply thenApply;
  private final ThenAccept thenAccept;
  private final ThenCombine thenCombine;
  private final ThenAcceptBoth thenAcceptBoth;

  CompletableFutureAdapter(D delegate) {
    this(delegate, CompletableFuture::thenApply, CompletableFuture::thenAccept,
        CompletableFuture::thenCombine, CompletableFuture::thenAcceptBoth);
  }

  CompletableFutureAdapter(D delegate, Void async) {
    this(delegate, CompletableFuture::thenApplyAsync, CompletableFuture::thenAcceptAsync,
        CompletableFuture::thenCombineAsync, CompletableFuture::thenAcceptBothAsync);
  }

  CompletableFutureAdapter(D delegate, Executor executor) {
    this(delegate, new AsyncThenApply(executor), new AsyncThenAccept(executor),
        new AsyncThenCombine(executor), new AsyncThenAcceptBoth(executor));
  }

  CompletableFutureAdapter(D delegate, CompletableFutureAdapter<?> parent) {
    this(delegate, parent.thenApply, parent.thenAccept, parent.thenCombine, parent.thenAcceptBoth);
  }

  private CompletableFutureAdapter(D delegate, ThenApply thenApply, ThenAccept thenAccept,
      ThenCombine thenCombine, ThenAcceptBoth thenAcceptBoth) {
    this.delegate = delegate;
    this.thenApply = thenApply;
    this.thenAccept = thenAccept;
    this.thenCombine = thenCombine;
    this.thenAcceptBoth = thenAcceptBoth;
  }

  protected D getDelegate() {
    return delegate;
  }

  protected <T, R> CompletableFuture<R> thenApply(CompletableFuture<T> future,
      Function<? super T, R> function) {
    return thenApply.thenApply(future, function);
  }

  protected <T> CompletableFuture<Void> thenAccept(CompletableFuture<T> future,
      Consumer<? super T> consumer) {
    return thenAccept.thenAccept(future, consumer);
  }

  protected <T, U, R> CompletableFuture<R> thenCombine(CompletableFuture<T> f1,
      CompletableFuture<U> f2, BiFunction<? super T, ? super U, ? extends R> function) {
    return thenCombine.thenCombine(f1, f2, function);
  }

  protected <T, U> CompletableFuture<Void> thenAcceptBoth(CompletableFuture<T> f1,
      CompletableFuture<U> f2, BiConsumer<? super T, ? super U> consumer) {
    return thenAcceptBoth.thenAcceptBoth(f1, f2, consumer);
  }

  @FunctionalInterface
  private static interface ThenApply {
    public <T, R> CompletableFuture<R> thenApply(CompletableFuture<T> future,
        Function<? super T, ? extends R> function);
  }

  @FunctionalInterface
  private static interface ThenAccept {
    public <T> CompletableFuture<Void> thenAccept(CompletableFuture<T> future,
        Consumer<? super T> consumer);
  }

  @FunctionalInterface
  private static interface ThenCombine {
    public <T, U, R> CompletableFuture<R> thenCombine(CompletableFuture<T> f1,
        CompletableFuture<U> f2, BiFunction<? super T, ? super U, ? extends R> function);
  }

  @FunctionalInterface
  private static interface ThenAcceptBoth {
    public <T, U> CompletableFuture<Void> thenAcceptBoth(CompletableFuture<T> f1,
        CompletableFuture<U> f2, BiConsumer<? super T, ? super U> consumer);
  }

  private static class AsyncThenApply implements ThenApply {
    private final Executor executor;

    public AsyncThenApply(Executor executor) {
      this.executor = executor;
    }

    @Override
    public <T, R> CompletableFuture<R> thenApply(CompletableFuture<T> future,
        Function<? super T, ? extends R> function) {
      return future.thenApplyAsync(function, executor);
    }
  }

  private static class AsyncThenAccept implements ThenAccept {
    private final Executor executor;

    public AsyncThenAccept(Executor executor) {
      this.executor = executor;
    }

    @Override
    public <T> CompletableFuture<Void> thenAccept(CompletableFuture<T> future,
        Consumer<? super T> consumer) {
      return future.thenAcceptAsync(consumer, executor);
    }
  }

  private static class AsyncThenCombine implements ThenCombine {
    private final Executor executor;

    public AsyncThenCombine(Executor executor) {
      this.executor = executor;
    }

    @Override
    public <T, U, R> CompletableFuture<R> thenCombine(CompletableFuture<T> f1,
        CompletableFuture<U> f2, BiFunction<? super T, ? super U, ? extends R> function) {
      return f1.thenCombineAsync(f2, function, executor);
    }
  }

  private static class AsyncThenAcceptBoth implements ThenAcceptBoth {
    private final Executor executor;

    public AsyncThenAcceptBoth(Executor executor) {
      this.executor = executor;
    }

    @Override
    public <T, U> CompletableFuture<Void> thenAcceptBoth(CompletableFuture<T> f1,
        CompletableFuture<U> f2, BiConsumer<? super T, ? super U> consumer) {
      return f1.thenAcceptBothAsync(f2, consumer, executor);
    }
  }
}
