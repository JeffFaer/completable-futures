package name.falgout.jeffrey.stream.future.adapter;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.Nothing;
import throwing.stream.ThrowingIntStream;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.union.UnionIntStream;

class FutureIntStreamAdapter extends
    FutureBaseStreamAdapter<Integer, UnionIntStream<FutureThrowable>, FutureIntStream> implements
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
  public FutureIntStream filter(IntPredicate predicate) {
    return chain(UnionIntStream::normalFilter, predicate);
  }

  @Override
  public FutureIntStream map(IntUnaryOperator mapper) {
    return chain(UnionIntStream::normalMap, mapper);
  }

  @Override
  public <U> FutureStream<U> mapToObj(IntFunction<? extends U> mapper) {
    return new FutureStreamAdapter<>(getDelegate().normalMapToObj(mapper), this);
  }

  @Override
  public FutureLongStream mapToLong(IntToLongFunction mapper) {
    return new FutureLongStreamAdapter(getDelegate().normalMapToLong(mapper), this);
  }

  @Override
  public FutureDoubleStream mapToDouble(IntToDoubleFunction mapper) {
    return new FutureDoubleStreamAdapter(getDelegate().normalMapToDouble(mapper), this);
  }

  @Override
  public FutureIntStream flatMap(IntFunction<? extends IntStream> mapper) {
    IntFunction<? extends ThrowingIntStream<Nothing>> f = i -> ThrowingBridge.of(mapper.apply(i));
    return chain(UnionIntStream::normalFlatMap, f);
  }

  @Override
  public FutureIntStream distinct() {
    return chain(UnionIntStream::distinct);
  }

  @Override
  public FutureIntStream sorted() {
    return chain(UnionIntStream::sorted);
  }

  @Override
  public FutureIntStream peek(IntConsumer action) {
    return chain(UnionIntStream::normalPeek, action);
  }

  @Override
  public FutureIntStream limit(long maxSize) {
    return chain(UnionIntStream::limit, maxSize);
  }

  @Override
  public FutureIntStream skip(long n) {
    return chain(UnionIntStream::skip, n);
  }

  @Override
  public void forEach(IntConsumer action) {
    completeVoid(UnionIntStream::normalForEach, action);
  }

  @Override
  public void forEachOrdered(IntConsumer action) {
    completeVoid(UnionIntStream::normalForEachOrdered, action);
  }

  @Override
  public Future<int[]> toArray() {
    return complete(UnionIntStream::toArray);
  }

  @Override
  public Future<Integer> reduce(int identity, IntBinaryOperator op) {
    return complete(s -> s.normalReduce(identity, op));
  }

  @Override
  public Future<OptionalInt> reduce(IntBinaryOperator op) {
    return complete(UnionIntStream::normalReduce, op);
  }

  @Override
  public <R> Future<R> collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator,
      BiConsumer<R, R> combiner) {
    return complete(s -> s.normalCollect(supplier, accumulator, combiner));
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
  public Future<Boolean> anyMatch(IntPredicate predicate) {
    return complete(UnionIntStream::normalAnyMatch, predicate);
  }

  @Override
  public Future<Boolean> allMatch(IntPredicate predicate) {
    return complete(UnionIntStream::normalAllMatch, predicate);
  }

  @Override
  public Future<Boolean> noneMatch(IntPredicate predicate) {
    return complete(UnionIntStream::normalNoneMatch, predicate);
  }

  @Override
  public Future<OptionalInt> findFirst() {
    return complete(UnionIntStream::findFirst);
  }

  @Override
  public Future<OptionalInt> findAny() {
    return complete(UnionIntStream::findAny);
  }

  @Override
  public FutureLongStream asLongStream() {
    return new FutureLongStreamAdapter(getDelegate().asLongStream(), this);
  }

  @Override
  public FutureDoubleStream asDoubleStream() {
    return new FutureDoubleStreamAdapter(getDelegate().asDoubleStream(), this);
  }

  @Override
  public FutureStream<Integer> boxed() {
    return new FutureStreamAdapter<Integer>(getDelegate().boxed(), this);
  }
}
