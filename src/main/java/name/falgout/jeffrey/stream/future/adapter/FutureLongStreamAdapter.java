package name.falgout.jeffrey.stream.future.adapter;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.Nothing;
import throwing.stream.ThrowingLongStream;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.union.UnionLongStream;

class FutureLongStreamAdapter extends
    FutureBaseStreamAdapter<Long, UnionLongStream<FutureThrowable>, FutureLongStream> implements
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
  public FutureLongStream filter(LongPredicate predicate) {
    return chain(UnionLongStream::normalFilter, predicate);
  }

  @Override
  public FutureLongStream map(LongUnaryOperator mapper) {
    return chain(UnionLongStream::normalMap, mapper);
  }

  @Override
  public <U> FutureStream<U> mapToObj(LongFunction<? extends U> mapper) {
    return new FutureStreamAdapter<>(getDelegate().normalMapToObj(mapper), this);
  }

  @Override
  public FutureIntStream mapToInt(LongToIntFunction mapper) {
    return new FutureIntStreamAdapter(getDelegate().normalMapToInt(mapper), this);
  }

  @Override
  public FutureDoubleStream mapToDouble(LongToDoubleFunction mapper) {
    return new FutureDoubleStreamAdapter(getDelegate().normalMapToDouble(mapper), this);
  }

  @Override
  public FutureLongStream flatMap(LongFunction<? extends LongStream> mapper) {
    LongFunction<? extends ThrowingLongStream<Nothing>> f = i -> ThrowingBridge.of(mapper.apply(i));
    return chain(UnionLongStream::normalFlatMap, f);
  }

  @Override
  public FutureLongStream distinct() {
    return chain(UnionLongStream::distinct);
  }

  @Override
  public FutureLongStream sorted() {
    return chain(UnionLongStream::sorted);
  }

  @Override
  public FutureLongStream peek(LongConsumer action) {
    return chain(UnionLongStream::normalPeek, action);
  }

  @Override
  public FutureLongStream limit(long maxSize) {
    return chain(UnionLongStream::limit, maxSize);
  }

  @Override
  public FutureLongStream skip(long n) {
    return chain(UnionLongStream::skip, n);
  }

  @Override
  public void forEach(LongConsumer action) {
    completeVoid(UnionLongStream::normalForEach, action);
  }

  @Override
  public void forEachOrdered(LongConsumer action) {
    completeVoid(UnionLongStream::normalForEachOrdered, action);
  }

  @Override
  public Future<long[]> toArray() {
    return complete(UnionLongStream::toArray);
  }

  @Override
  public Future<Long> reduce(long identity, LongBinaryOperator op) {
    return complete(s -> s.normalReduce(identity, op));
  }

  @Override
  public Future<OptionalLong> reduce(LongBinaryOperator op) {
    return complete(UnionLongStream::normalReduce, op);
  }

  @Override
  public <R> Future<R> collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator,
      BiConsumer<R, R> combiner) {
    return complete(s -> s.normalCollect(supplier, accumulator, combiner));
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
  public Future<Boolean> anyMatch(LongPredicate predicate) {
    return complete(UnionLongStream::normalAnyMatch, predicate);
  }

  @Override
  public Future<Boolean> allMatch(LongPredicate predicate) {
    return complete(UnionLongStream::normalAllMatch, predicate);
  }

  @Override
  public Future<Boolean> noneMatch(LongPredicate predicate) {
    return complete(UnionLongStream::normalNoneMatch, predicate);
  }

  @Override
  public Future<OptionalLong> findFirst() {
    return complete(UnionLongStream::findFirst);
  }

  @Override
  public Future<OptionalLong> findAny() {
    return complete(UnionLongStream::findAny);
  }

  @Override
  public FutureDoubleStream asDoubleStream() {
    return new FutureDoubleStreamAdapter(getDelegate().asDoubleStream(), this);
  }

  @Override
  public FutureStream<Long> boxed() {
    return new FutureStreamAdapter<Long>(getDelegate().boxed(), this);
  }
}
