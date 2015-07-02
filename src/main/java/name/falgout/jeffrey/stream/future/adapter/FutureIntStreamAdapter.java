package name.falgout.jeffrey.stream.future.adapter;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingIntBinaryOperator;
import throwing.function.ThrowingIntConsumer;
import throwing.function.ThrowingIntFunction;
import throwing.function.ThrowingIntPredicate;
import throwing.function.ThrowingObjIntConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.adapter.ThrowingIntStreamIntermediateAdapter;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;

class FutureIntStreamAdapter extends
    FutureBaseStreamAdapter<Integer, UnionIntStream<FutureThrowable>, FutureIntStream> implements
    ThrowingIntStreamIntermediateAdapter<Throwable, ExecutionException, UnionIntStream<FutureThrowable>, UnionLongStream<FutureThrowable>, UnionDoubleStream<FutureThrowable>, FutureIntStream, FutureLongStream, FutureDoubleStream>,
    FutureIntStream {

  FutureIntStreamAdapter(UnionIntStream<FutureThrowable> delegate, Executor executor) {
    super(delegate, executor);
  }

  FutureIntStreamAdapter(UnionIntStream<FutureThrowable> delegate,
      FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate, parent);
  }

  FutureIntStreamAdapter(UnionIntStream<FutureThrowable> delegate) {
    super(delegate);
  }

  @Override
  public FutureIntStream getSelf() {
    return this;
  }

  @Override
  public FutureIntStream createNewAdapter(UnionIntStream<FutureThrowable> newDelegate) {
    return new FutureIntStreamAdapter(newDelegate, this);
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
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.Iterator<Integer, Throwable, Throwable> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.BaseSpliterator<Integer, Throwable, Throwable, ?> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  };

  @Override
  public FutureStream<Integer> boxed() {
    return new FutureStreamAdapter<Integer>(getDelegate().boxed(), this);
  }

  @Override
  public <U> FutureStream<U> mapToObj(ThrowingIntFunction<? extends U, ? extends Throwable> mapper) {
    return new FutureStreamAdapter<>(getDelegate().mapToObj(getFunctionAdapter().convert(mapper)),
        this);
  }

  @Override
  public Future<Void> forEach(ThrowingIntConsumer<?> action) {
    return completeVoid(UnionIntStream::forEach, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<Void> forEachOrdered(ThrowingIntConsumer<?> action) {
    return completeVoid(UnionIntStream::forEachOrdered, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<int[]> toArray() {
    return complete(UnionIntStream::toArray);
  }

  @Override
  public Future<Integer> reduce(int identity, ThrowingIntBinaryOperator<?> op) {
    return complete(s -> s.reduce(identity, getFunctionAdapter().convert(op)));
  }

  @Override
  public Future<OptionalInt> reduce(ThrowingIntBinaryOperator<?> op) {
    return complete(UnionIntStream::reduce, getFunctionAdapter().convert(op));
  }

  @Override
  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingObjIntConsumer<R, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner) {
    return complete(s -> s.collect(getFunctionAdapter().convert(supplier),
        getFunctionAdapter().convert(accumulator), getFunctionAdapter().convert(combiner)));
  }

  @Override
  public Future<Integer> sum() {
    return complete(UnionIntStream::sum);
  }

  @Override
  public Future<OptionalInt> min() {
    return complete(UnionIntStream::min);
  }

  @Override
  public Future<OptionalInt> max() {
    return complete(UnionIntStream::max);
  }

  @Override
  public Future<Long> count() {
    return complete(UnionIntStream::count);
  }

  @Override
  public Future<OptionalDouble> average() {
    return complete(UnionIntStream::average);
  }

  @Override
  public Future<IntSummaryStatistics> summaryStatistics() {
    return complete(UnionIntStream::summaryStatistics);
  }

  @Override
  public Future<Boolean> anyMatch(ThrowingIntPredicate<?> predicate) {
    return complete(UnionIntStream::anyMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> allMatch(ThrowingIntPredicate<?> predicate) {
    return complete(UnionIntStream::allMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> noneMatch(ThrowingIntPredicate<?> predicate) {
    return complete(UnionIntStream::noneMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<OptionalInt> findFirst() {
    return complete(UnionIntStream::findFirst);
  }

  @Override
  public Future<OptionalInt> findAny() {
    return complete(UnionIntStream::findAny);
  }
}
