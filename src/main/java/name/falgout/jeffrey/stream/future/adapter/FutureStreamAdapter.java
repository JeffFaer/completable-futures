package name.falgout.jeffrey.stream.future.adapter;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntFunction;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.ThrowingComparator;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingBiFunction;
import throwing.function.ThrowingBinaryOperator;
import throwing.function.ThrowingConsumer;
import throwing.function.ThrowingFunction;
import throwing.function.ThrowingPredicate;
import throwing.function.ThrowingSupplier;
import throwing.stream.ThrowingCollector;
import throwing.stream.ThrowingStream;
import throwing.stream.intermediate.adapter.ThrowingStreamIntermediateAdapter;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;
import throwing.stream.union.UnionStream;

class FutureStreamAdapter<T> extends
    FutureBaseStreamAdapter<T, UnionStream<T, FutureThrowable>, FutureStream<T>> implements
    FutureStream<T>,
    ThrowingStreamIntermediateAdapter<T, Throwable, ExecutionException, UnionStream<T, FutureThrowable>, UnionIntStream<FutureThrowable>, UnionLongStream<FutureThrowable>, UnionDoubleStream<FutureThrowable>, FutureStream<T>, FutureIntStream, FutureLongStream, FutureDoubleStream> {
  FutureStreamAdapter(UnionStream<T, FutureThrowable> delegate, Executor executor) {
    super(delegate, executor);
  }

  FutureStreamAdapter(UnionStream<T, FutureThrowable> delegate,
      FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate, parent);
  }

  FutureStreamAdapter(UnionStream<T, FutureThrowable> delegate) {
    super(delegate);
  }

  @Override
  public FutureStream<T> getSelf() {
    return this;
  }

  @Override
  public FutureStream<T> createNewAdapter(UnionStream<T, FutureThrowable> delegate) {
    return newStream(delegate);
  }

  private <R> FutureStreamAdapter<R> newStream(UnionStream<R, FutureThrowable> delegate) {
    return new FutureStreamAdapter<>(delegate);
  }

  @Override
  public FutureIntStream newIntStream(UnionIntStream<FutureThrowable> delegate) {
    return new FutureIntStreamAdapter(delegate, this);
  }

  @Override
  public FutureLongStream newLongStream(UnionLongStream<FutureThrowable> delegate) {
    return new FutureLongStreamAdapter(delegate, this);
  }

  @Override
  public FutureDoubleStream newDoubleStream(UnionDoubleStream<FutureThrowable> delegate) {
    return new FutureDoubleStreamAdapter(delegate, this);
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.Iterator<T, Throwable, Throwable> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.BaseSpliterator<T, Throwable, Throwable, ?> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public <R> FutureStream<R> map(
      ThrowingFunction<? super T, ? extends R, ? extends Throwable> mapper) {
    return newStream(getDelegate().map(getFunctionAdapter().convert(mapper)));
  }

  @Override
  public <R> FutureStream<R> flatMap(
      ThrowingFunction<? super T, ? extends ThrowingStream<? extends R, ? extends Throwable>, ? extends Throwable> mapper) {
    Function<ThrowingStream<? extends R, ? extends Throwable>, ThrowingStream<? extends R, ExecutionException>> streamMapper = getFunctionAdapter()::convert;
    return newStream(getDelegate().flatMap(
        getFunctionAdapter().convert(mapper.andThen(streamMapper))));
  }

  @Override
  public Future<Void> forEach(ThrowingConsumer<? super T, ?> action) {
    return completeVoid(UnionStream::forEach, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<Void> forEachOrdered(ThrowingConsumer<? super T, ?> action) {
    return completeVoid(UnionStream::forEachOrdered, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<Object[]> toArray() {
    return complete(UnionStream::toArray);
  }

  @Override
  public <A> Future<A[]> toArray(IntFunction<A[]> generator) {
    return complete(UnionStream::toArray, generator);
  }

  @Override
  public Future<T> reduce(T identity, ThrowingBinaryOperator<T, ?> accumulator) {
    return complete(s -> s.reduce(identity, getFunctionAdapter().convert(accumulator)));
  }

  @Override
  public Future<Optional<T>> reduce(ThrowingBinaryOperator<T, ?> accumulator) {
    return complete(UnionStream<T, FutureThrowable>::reduce,
        getFunctionAdapter().convert(accumulator));
  }

  @Override
  public <U> Future<U> reduce(U identity, ThrowingBiFunction<U, ? super T, U, ?> accumulator,
      ThrowingBinaryOperator<U, ?> combiner) {
    return complete(s -> s.reduce(identity, getFunctionAdapter().convert(accumulator),
        getFunctionAdapter().convert(combiner)));
  }

  @Override
  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingBiConsumer<R, ? super T, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner) {
    return complete(s -> s.collect(getFunctionAdapter().convert(supplier),
        getFunctionAdapter().convert(accumulator), getFunctionAdapter().convert(combiner)));
  }

  @Override
  public <R, A> Future<R> collect(ThrowingCollector<? super T, A, R, ?> collector) {
    return complete(UnionStream::collect, getFunctionAdapter().convert(collector));
  }

  @Override
  public Future<Optional<T>> min(ThrowingComparator<? super T, ?> comparator) {
    return complete(UnionStream<T, FutureThrowable>::min, getFunctionAdapter().convert(comparator));
  }

  @Override
  public Future<Optional<T>> max(ThrowingComparator<? super T, ?> comparator) {
    return complete(UnionStream<T, FutureThrowable>::max, getFunctionAdapter().convert(comparator));
  }

  @Override
  public Future<Long> count() {
    return complete(UnionStream::count);
  }

  @Override
  public Future<Boolean> anyMatch(ThrowingPredicate<? super T, ?> predicate) {
    return complete(UnionStream<T, FutureThrowable>::anyMatch,
        getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> allMatch(ThrowingPredicate<? super T, ?> predicate) {
    return complete(UnionStream<T, FutureThrowable>::allMatch,
        getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> noneMatch(ThrowingPredicate<? super T, ?> predicate) {
    return complete(UnionStream<T, FutureThrowable>::noneMatch,
        getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Optional<T>> findFirst() {
    return complete(UnionStream::findFirst);
  }

  @Override
  public Future<Optional<T>> findAny() {
    return complete(UnionStream::findAny);
  }
}
