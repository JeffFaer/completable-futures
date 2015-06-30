package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;

import name.falgout.jeffrey.stream.future.FutureBaseStream;
import name.falgout.jeffrey.stream.future.FutureIterator;
import name.falgout.jeffrey.stream.future.FutureSpliterator;

abstract class FutureBaseStreamAdapter<T, S extends FutureBaseStream<T, S>, S2 extends BaseStream<T, S2>> extends
    CompletableFutureAdapter<CompletableFuture<S2>> implements FutureBaseStream<T, S> {
  FutureBaseStreamAdapter(CompletableFuture<S2> delegate, Executor executor) {
    super(delegate, executor);
  }

  FutureBaseStreamAdapter(CompletableFuture<S2> delegate, Void async) {
    super(delegate, async);
  }

  FutureBaseStreamAdapter(CompletableFuture<S2> delegate) {
    super(delegate);
  }

  FutureBaseStreamAdapter(CompletableFuture<S2> delegate, FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate, parent);
  }

  protected S chain(CompletableFuture<S2> delegate) {
    if (getDelegate().equals(delegate)) {
      return getSelf();
    } else {
      return newStream(delegate);
    }
  }

  protected <R> CompletableFuture<R> thenApply(Function<? super S2, R> function) {
    return thenApply(getDelegate(), function);
  }

  protected CompletableFuture<Void> thenAccept(Consumer<? super S2> consumer) {
    return thenAccept(getDelegate(), consumer);
  }

  protected abstract S getSelf();

  protected abstract S newStream(CompletableFuture<S2> delegate);

  @Override
  public boolean isParallel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public S sequential() {
    return chain(thenApply(S2::sequential));
  }

  @Override
  public S parallel() {
    return chain(thenApply(S2::parallel));
  }

  @Override
  public S unordered() {
    return chain(thenApply(S2::unordered));
  }

  @Override
  public S onClose(Runnable closeHandler) {
    return chain(thenApply(s -> s.onClose(closeHandler)));
  }

  @Override
  public void close() {
    thenAccept(S2::close);
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
}
