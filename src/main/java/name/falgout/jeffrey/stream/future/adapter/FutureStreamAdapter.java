package name.falgout.jeffrey.stream.future.adapter;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

class FutureStreamAdapter<T> extends FutureBaseStreamAdapter<T, FutureStream<T>, Stream<T>>
        implements FutureStream<T> {
    FutureStreamAdapter(CompletableFuture<Stream<T>> delegate) {
        super(delegate);
    }

    FutureStreamAdapter(CompletableFuture<Stream<T>> delegate, Void async) {
        super(delegate, async);
    }

    FutureStreamAdapter(CompletableFuture<Stream<T>> delegate, Executor executor) {
        super(delegate, executor);
    }

    FutureStreamAdapter(CompletableFuture<Stream<T>> delegate,
            FutureBaseStreamAdapter<?, ?, ?> parent) {
        super(delegate, parent);
    }

    @Override
    protected FutureStreamAdapter<T> getSelf() {
        return this;
    }

    @Override
    protected FutureStreamAdapter<T> newStream(CompletableFuture<Stream<T>> delegate) {
        return createNewStream(delegate);
    }

    private <R> FutureStreamAdapter<R> createNewStream(CompletableFuture<Stream<R>> delegate) {
        return new FutureStreamAdapter<R>(delegate, this);
    }

    @Override
    public FutureStream<T> filter(Predicate<? super T> predicate) {
        return chain(thenApply(s -> s.filter(predicate)));
    }

    @Override
    public <R> FutureStream<R> map(Function<? super T, ? extends R> mapper) {
        return createNewStream(thenApply(s -> s.map(mapper)));
    }

    @Override
    public FutureIntStream mapToInt(ToIntFunction<? super T> mapper) {
        return new FutureIntStreamAdapter(thenApply(s -> s.mapToInt(mapper)), this);
    }

    @Override
    public FutureLongStream mapToLong(ToLongFunction<? super T> mapper) {
        return new FutureLongStreamAdapter(thenApply(s -> s.mapToLong(mapper)), this);
    }

    @Override
    public FutureDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return new FutureDoubleStreamAdapter(thenApply(s -> s.mapToDouble(mapper)), this);
    }

    @Override
    public <R> FutureStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return createNewStream(thenApply(s -> s.flatMap(mapper)));
    }

    @Override
    public FutureIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return new FutureIntStreamAdapter(thenApply(s -> s.flatMapToInt(mapper)), this);
    }

    @Override
    public FutureLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return new FutureLongStreamAdapter(thenApply(s -> s.flatMapToLong(mapper)), this);
    }

    @Override
    public FutureDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return new FutureDoubleStreamAdapter(thenApply(s -> s.flatMapToDouble(mapper)), this);
    }

    @Override
    public FutureStream<T> distinct() {
        return chain(thenApply(Stream::distinct));
    }

    @Override
    public FutureStream<T> sorted() {
        return chain(thenApply(Stream::sorted));
    }

    @Override
    public FutureStream<T> sorted(Comparator<? super T> comparator) {
        return chain(thenApply(s -> s.sorted(comparator)));
    }

    @Override
    public FutureStream<T> peek(Consumer<? super T> action) {
        return chain(thenApply(s -> s.peek(action)));
    }

    @Override
    public FutureStream<T> limit(long maxSize) {
        return chain(thenApply(s -> s.limit(maxSize)));
    }

    @Override
    public FutureStream<T> skip(long n) {
        return chain(thenApply(s -> s.skip(n)));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        thenAccept(s -> s.forEach(action));
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        thenAccept(s -> s.forEachOrdered(action));
    }

    @Override
    public Future<Object[]> toArray() {
        return thenApply(Stream::toArray);
    }

    @Override
    public <A> Future<A[]> toArray(IntFunction<A[]> generator) {
        return thenApply(s -> s.toArray(generator));
    }

    @Override
    public Future<T> reduce(T identity, BinaryOperator<T> accumulator) {
        return thenApply(s -> s.reduce(identity, accumulator));
    }

    @Override
    public Future<Optional<T>> reduce(BinaryOperator<T> accumulator) {
        return thenApply(s -> s.reduce(accumulator));
    }

    @Override
    public <U> Future<U> reduce(U identity, BiFunction<U, ? super T, U> accumulator,
            BinaryOperator<U> combiner) {
        return thenApply(s -> s.reduce(identity, accumulator, combiner));
    }

    @Override
    public <R> Future<R> collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator,
            BiConsumer<R, R> combiner) {
        return thenApply(s -> s.collect(supplier, accumulator, combiner));
    }

    @Override
    public <R, A> Future<R> collect(Collector<? super T, A, R> collector) {
        return thenApply(s -> s.collect(collector));
    }

    @Override
    public Future<Optional<T>> min(Comparator<? super T> comparator) {
        return thenApply(s -> s.min(comparator));
    }

    @Override
    public Future<Optional<T>> max(Comparator<? super T> comparator) {
        return thenApply(s -> s.max(comparator));
    }

    @Override
    public Future<Long> count() {
        return thenApply(Stream::count);
    }

    @Override
    public Future<Boolean> anyMatch(Predicate<? super T> predicate) {
        return thenApply(s -> s.anyMatch(predicate));
    }

    @Override
    public Future<Boolean> allMatch(Predicate<? super T> predicate) {
        return thenApply(s -> s.allMatch(predicate));
    }

    @Override
    public Future<Boolean> noneMatch(Predicate<? super T> predicate) {
        return thenApply(s -> s.noneMatch(predicate));
    }

    @Override
    public Future<Optional<T>> findFirst() {
        return thenApply(Stream::findFirst);
    }

    @Override
    public Future<Optional<T>> findAny() {
        return thenApply(Stream::findAny);
    }
}
