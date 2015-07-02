package name.falgout.jeffrey.stream.future.adapter;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.Nothing;
import throwing.stream.ThrowingDoubleStream;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.union.UnionDoubleStream;

class FutureDoubleStreamAdapter extends
    FutureBaseStreamAdapter<Double, UnionDoubleStream<FutureThrowable>, FutureDoubleStream> implements
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
  public FutureDoubleStream filter(DoublePredicate predicate) {
    return chain(UnionDoubleStream::normalFilter, predicate);
  }

  @Override
  public FutureDoubleStream map(DoubleUnaryOperator mapper) {
    return chain(UnionDoubleStream::normalMap, mapper);
  }

  @Override
  public <U> FutureStream<U> mapToObj(DoubleFunction<? extends U> mapper) {
    return new FutureStreamAdapter<>(getDelegate().normalMapToObj(mapper), this);
  }

  @Override
  public FutureLongStream mapToLong(DoubleToLongFunction mapper) {
    return new FutureLongStreamAdapter(getDelegate().normalMapToLong(mapper), this);
  }

  @Override
  public FutureIntStream mapToInt(DoubleToIntFunction mapper) {
    return new FutureIntStreamAdapter(getDelegate().normalMapToInt(mapper), this);
  }

  @Override
  public FutureDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
    DoubleFunction<? extends ThrowingDoubleStream<Nothing>> f = i -> ThrowingBridge.of(mapper.apply(i));
    return chain(UnionDoubleStream::normalFlatMap, f);
  }

  @Override
  public FutureDoubleStream distinct() {
    return chain(UnionDoubleStream::distinct);
  }

  @Override
  public FutureDoubleStream sorted() {
    return chain(UnionDoubleStream::sorted);
  }

  @Override
  public FutureDoubleStream peek(DoubleConsumer action) {
    return chain(UnionDoubleStream::normalPeek, action);
  }

  @Override
  public FutureDoubleStream limit(long maxSize) {
    return chain(UnionDoubleStream::limit, maxSize);
  }

  @Override
  public FutureDoubleStream skip(long n) {
    return chain(UnionDoubleStream::skip, n);
  }

  @Override
  public void forEach(DoubleConsumer action) {
    completeVoid(UnionDoubleStream::normalForEach, action);
  }

  @Override
  public void forEachOrdered(DoubleConsumer action) {
    completeVoid(UnionDoubleStream::normalForEachOrdered, action);
  }

  @Override
  public Future<double[]> toArray() {
    return complete(UnionDoubleStream::toArray);
  }

  @Override
  public Future<Double> reduce(double identity, DoubleBinaryOperator op) {
    return complete(s -> s.normalReduce(identity, op));
  }

  @Override
  public Future<OptionalDouble> reduce(DoubleBinaryOperator op) {
    return complete(UnionDoubleStream::normalReduce, op);
  }

  @Override
  public <R> Future<R> collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator,
      BiConsumer<R, R> combiner) {
    return complete(s -> s.normalCollect(supplier, accumulator, combiner));
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
  public Future<Boolean> anyMatch(DoublePredicate predicate) {
    return complete(UnionDoubleStream::normalAnyMatch, predicate);
  }

  @Override
  public Future<Boolean> allMatch(DoublePredicate predicate) {
    return complete(UnionDoubleStream::normalAllMatch, predicate);
  }

  @Override
  public Future<Boolean> noneMatch(DoublePredicate predicate) {
    return complete(UnionDoubleStream::normalNoneMatch, predicate);
  }

  @Override
  public Future<OptionalDouble> findFirst() {
    return complete(UnionDoubleStream::findFirst);
  }

  @Override
  public Future<OptionalDouble> findAny() {
    return complete(UnionDoubleStream::findAny);
  }

  @Override
  public FutureStream<Double> boxed() {
    return new FutureStreamAdapter<Double>(getDelegate().boxed(), this);
  }
}
