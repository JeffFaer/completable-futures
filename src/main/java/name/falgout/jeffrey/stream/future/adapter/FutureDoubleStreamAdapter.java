package name.falgout.jeffrey.stream.future.adapter;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingDoubleBinaryOperator;
import throwing.function.ThrowingDoubleConsumer;
import throwing.function.ThrowingDoubleFunction;
import throwing.function.ThrowingDoublePredicate;
import throwing.function.ThrowingObjDoubleConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.adapter.ThrowingDoubleStreamIntermediateAdapter;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;

class FutureDoubleStreamAdapter extends
    FutureBaseStreamAdapter<Double, UnionDoubleStream<FutureThrowable>, FutureDoubleStream> implements
    ThrowingDoubleStreamIntermediateAdapter<Throwable, ExecutionException, UnionIntStream<FutureThrowable>, UnionLongStream<FutureThrowable>, UnionDoubleStream<FutureThrowable>, FutureIntStream, FutureLongStream, FutureDoubleStream>,
    FutureDoubleStream {

  FutureDoubleStreamAdapter(UnionDoubleStream<FutureThrowable> delegate, Executor executor) {
    super(delegate, executor);
  }

  FutureDoubleStreamAdapter(UnionDoubleStream<FutureThrowable> delegate,
      FutureBaseStreamAdapter<?, ?, ?> parent) {
    super(delegate, parent);
  }

  FutureDoubleStreamAdapter(UnionDoubleStream<FutureThrowable> delegate) {
    super(delegate);
  }

  @Override
  public FutureDoubleStream getSelf() {
    return this;
  }

  @Override
  public FutureDoubleStream createNewAdapter(UnionDoubleStream<FutureThrowable> newDelegate) {
    return new FutureDoubleStreamAdapter(newDelegate, this);
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
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.Iterator<Double, Throwable, Throwable> iterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public throwing.stream.terminal.ThrowingBaseStreamTerminal.BaseSpliterator<Double, Throwable, Throwable, ?> spliterator() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("TODO");
  };

  @Override
  public FutureStream<Double> boxed() {
    return new FutureStreamAdapter<Double>(getDelegate().boxed(), this);
  }

  @Override
  public <U> FutureStream<U> mapToObj(
      ThrowingDoubleFunction<? extends U, ? extends Throwable> mapper) {
    return new FutureStreamAdapter<>(getDelegate().mapToObj(getFunctionAdapter().convert(mapper)),
        this);
  }

  @Override
  public Future<Void> forEach(ThrowingDoubleConsumer<?> action) {
    return completeVoid(UnionDoubleStream::forEach, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<Void> forEachOrdered(ThrowingDoubleConsumer<?> action) {
    return completeVoid(UnionDoubleStream::forEachOrdered, getFunctionAdapter().convert(action));
  }

  @Override
  public Future<double[]> toArray() {
    return complete(UnionDoubleStream::toArray);
  }

  @Override
  public Future<Double> reduce(double identity, ThrowingDoubleBinaryOperator<?> op) {
    return complete(s -> s.reduce(identity, getFunctionAdapter().convert(op)));
  }

  @Override
  public Future<OptionalDouble> reduce(ThrowingDoubleBinaryOperator<?> op) {
    return complete(UnionDoubleStream::reduce, getFunctionAdapter().convert(op));
  }

  @Override
  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingObjDoubleConsumer<R, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner) {
    return complete(s -> s.collect(getFunctionAdapter().convert(supplier),
        getFunctionAdapter().convert(accumulator), getFunctionAdapter().convert(combiner)));
  }

  @Override
  public Future<Double> sum() {
    return complete(UnionDoubleStream::sum);
  }

  @Override
  public Future<OptionalDouble> min() {
    return complete(UnionDoubleStream::min);
  }

  @Override
  public Future<OptionalDouble> max() {
    return complete(UnionDoubleStream::max);
  }

  @Override
  public Future<Long> count() {
    return complete(UnionDoubleStream::count);
  }

  @Override
  public Future<OptionalDouble> average() {
    return complete(UnionDoubleStream::average);
  }

  @Override
  public Future<DoubleSummaryStatistics> summaryStatistics() {
    return complete(UnionDoubleStream::summaryStatistics);
  }

  @Override
  public Future<Boolean> anyMatch(ThrowingDoublePredicate<?> predicate) {
    return complete(UnionDoubleStream::anyMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> allMatch(ThrowingDoublePredicate<?> predicate) {
    return complete(UnionDoubleStream::allMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<Boolean> noneMatch(ThrowingDoublePredicate<?> predicate) {
    return complete(UnionDoubleStream::noneMatch, getFunctionAdapter().convert(predicate));
  }

  @Override
  public Future<OptionalDouble> findFirst() {
    return complete(UnionDoubleStream::findFirst);
  }

  @Override
  public Future<OptionalDouble> findAny() {
    return complete(UnionDoubleStream::findAny);
  }
}
