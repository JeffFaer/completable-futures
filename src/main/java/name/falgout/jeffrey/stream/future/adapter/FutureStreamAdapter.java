package name.falgout.jeffrey.stream.future.adapter;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.Nothing;
import throwing.stream.ThrowingStream;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.union.UnionStream;

class FutureStreamAdapter<T> extends
    FutureBaseStreamAdapter<T, UnionStream<T, FutureThrowable>, FutureStream<T>> implements
    FutureStream<T> {
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
  public FutureStream<T> filter(Predicate<? super T> predicate) {
    return chain(getDelegate().normalFilter(predicate));
  }

  @Override
  public <R> FutureStream<R> map(Function<? super T, ? extends R> mapper) {
    return newStream(getDelegate().normalMap(mapper));
  }

  @Override
  public FutureIntStream mapToInt(ToIntFunction<? super T> mapper) {
    return new FutureIntStreamAdapter(getDelegate().normalMapToInt(mapper), this);
  }

  @Override
  public FutureLongStream mapToLong(ToLongFunction<? super T> mapper) {
    return new FutureLongStreamAdapter(getDelegate().normalMapToLong(mapper), this);
  }

  @Override
  public FutureDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
    return new FutureDoubleStreamAdapter(getDelegate().normalMapToDouble(mapper), this);
  }

  @Override
  public <R> FutureStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
    Function<Stream<? extends R>, ThrowingStream<? extends R, Nothing>> streamMapper = ThrowingBridge::of;
    return newStream(getDelegate().normalFlatMap(mapper.andThen(streamMapper)));
  }

  @Override
  public FutureIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
    return new FutureIntStreamAdapter(getDelegate().normalFlatMapToInt(
        mapper.andThen(ThrowingBridge::of)), this);
  }

  @Override
  public FutureLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
    return new FutureLongStreamAdapter(getDelegate().normalFlatMapToLong(
        mapper.andThen(ThrowingBridge::of)), this);
  }

  @Override
  public FutureDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
    return new FutureDoubleStreamAdapter(getDelegate().normalFlatMapToDouble(
        mapper.andThen(ThrowingBridge::of)), this);
  }

  @Override
  public FutureStream<T> distinct() {
    return chain(UnionStream::distinct);
  }

  @Override
  public FutureStream<T> sorted() {
    return chain(UnionStream::sorted);
  }

  @Override
  public FutureStream<T> sorted(Comparator<? super T> comparator) {
    return chain(UnionStream::normalSorted, comparator);
  }

  @Override
  public FutureStream<T> peek(Consumer<? super T> action) {
    return chain(UnionStream::normalPeek, action);
  }

  @Override
  public FutureStream<T> limit(long maxSize) {
    return chain(UnionStream::limit, maxSize);
  }

  @Override
  public FutureStream<T> skip(long n) {
    return chain(UnionStream::skip, n);
  }

  @Override
  public void forEach(Consumer<? super T> action) {
    completeVoid(UnionStream::normalForEach, action);
  }

  @Override
  public void forEachOrdered(Consumer<? super T> action) {
    completeVoid(UnionStream::normalForEachOrdered, action);
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
  public Future<T> reduce(T identity, BinaryOperator<T> accumulator) {
    return complete(s -> s.normalReduce(identity, accumulator));
  }

  @Override
  public Future<Optional<T>> reduce(BinaryOperator<T> accumulator) {
    return complete(s -> s.normalReduce(accumulator));
  }

  @Override
  public <U> Future<U> reduce(U identity, BiFunction<U, ? super T, U> accumulator,
      BinaryOperator<U> combiner) {
    return complete(s -> s.normalReduce(identity, accumulator, combiner));
  }

  @Override
  public <R> Future<R> collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator,
      BiConsumer<R, R> combiner) {
    return complete(s -> s.normalCollect(supplier, accumulator, combiner));
  }

  @Override
  public <R, A> Future<R> collect(Collector<? super T, A, R> collector) {
    return complete(UnionStream::collect, collector);
  }

  @Override
  public Future<Optional<T>> min(Comparator<? super T> comparator) {
    return complete(UnionStream::normalMin, comparator);
  }

  @Override
  public Future<Optional<T>> max(Comparator<? super T> comparator) {
    return complete(UnionStream::normalMax, comparator);
  }

  @Override
  public Future<Long> count() {
    return complete(UnionStream::count);
  }

  @Override
  public Future<Boolean> anyMatch(Predicate<? super T> predicate) {
    return complete(UnionStream::normalAnyMatch, predicate);
  }

  @Override
  public Future<Boolean> allMatch(Predicate<? super T> predicate) {
    return complete(UnionStream::normalAllMatch, predicate);
  }

  @Override
  public Future<Boolean> noneMatch(Predicate<? super T> predicate) {
    return complete(UnionStream::normalNoneMatch, predicate);
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
