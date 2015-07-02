package name.falgout.jeffrey.stream.future.adapter;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingLongBinaryOperator;
import throwing.function.ThrowingLongConsumer;
import throwing.function.ThrowingLongFunction;
import throwing.function.ThrowingLongPredicate;
import throwing.function.ThrowingObjLongConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.adapter.ThrowingLongStreamIntermediateAdapter;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;

class FutureLongStreamAdapter extends
    FutureBaseStreamAdapter<Long, UnionLongStream<FutureThrowable>, FutureLongStream> implements
    ThrowingLongStreamIntermediateAdapter<Throwable, ExecutionException, UnionIntStream<FutureThrowable>, UnionLongStream<FutureThrowable>, UnionDoubleStream<FutureThrowable>, FutureIntStream, FutureLongStream, FutureDoubleStream>,
    FutureLongStream {

  FutureLongStreamAdapter(UnionLongStream<FutureThrowable> delegate, Executor executor) {
    super(delegate, executor);
  }

  FutureLongStreamAdapter(UnionLongStream<FutureThrowable> delegate,
      FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate, parent);
  }

  FutureLongStreamAdapter(UnionLongStream<FutureThrowable> delegate) {
    super(delegate);
  }

  @Override
  public FutureLongStream getSelf() {
    return this;
  }

  @Override
  public FutureLongStream createNewAdapter(UnionLongStream<FutureThrowable> newDelegate) {
    return new FutureLongStreamAdapter(newDelegate, this);
  }

  @Override
  public FutureIntStream newIntStream(UnionIntStream<FutureThrowable> delegate) {
    return new FutureIntStreamAdapter(delegate, this);
  }

  @Override
  public FutureDoubleStream newDoubleStream(UnionDoubleStream<FutureThrowable> delegate) {
    return new FutureDoubleStreamAdapter(delegate, this);
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.Iterator<Long, Throwable, Throwable> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.BaseSpliterator<Long, Throwable, Throwable, ?> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  };

  @Override
  public FutureStream<Long> boxed() {
    return new FutureStreamAdapter<Long>(getDelegate().boxed(), this);
  }

  @Override
  public <U> FutureStream<U> mapToObj(ThrowingLongFunction<? extends U, ? extends Throwable> mapper) {
    return new FutureStreamAdapter<>(getDelegate().mapToObj(getFunctionAdapter().convert(mapper)),
        this);
  }

  @Override
  public Future<Void> forEach(ThrowingLongConsumer<?> action) {
    return completeVoid(UnionLongStream::forEach, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<Void> forEachOrdered(ThrowingLongConsumer<?> action) {
    return completeVoid(UnionLongStream::forEachOrdered, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<long[]> toArray() {
    return complete(UnionLongStream::toArray);
  }

  @Override
  public Future<Long> reduce(long identity, ThrowingLongBinaryOperator<?> op) {
    return complete(s -> s.reduce(identity, getFunctionAdapter().convert(op)));
  }

  @Override
  public Future<OptionalLong> reduce(ThrowingLongBinaryOperator<?> op) {
    return complete(UnionLongStream::reduce, getFunctionAdapter().convert(op));
  }

  @Override
  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingObjLongConsumer<R, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner) {
    return complete(s -> s.collect(getFunctionAdapter().convert(supplier),
        getFunctionAdapter().convert(accumulator), getFunctionAdapter().convert(combiner)));
  }

  @Override
  public Future<Long> sum() {
    return complete(UnionLongStream::sum);
  }

  @Override
  public Future<OptionalLong> min() {
    return complete(UnionLongStream::min);
  }

  @Override
  public Future<OptionalLong> max() {
    return complete(UnionLongStream::max);
  }

  @Override
  public Future<Long> count() {
    return complete(UnionLongStream::count);
  }

  @Override
  public Future<OptionalDouble> average() {
    return complete(UnionLongStream::average);
  }

  @Override
  public Future<LongSummaryStatistics> summaryStatistics() {
    return complete(UnionLongStream::summaryStatistics);
  }

  @Override
  public Future<Boolean> anyMatch(ThrowingLongPredicate<?> predicate) {
    return complete(UnionLongStream::anyMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> allMatch(ThrowingLongPredicate<?> predicate) {
    return complete(UnionLongStream::allMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> noneMatch(ThrowingLongPredicate<?> predicate) {
    return complete(UnionLongStream::noneMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<OptionalLong> findFirst() {
    return complete(UnionLongStream::findFirst);
  }

  @Override
  public Future<OptionalLong> findAny() {
    return complete(UnionLongStream::findAny);
  }
}
