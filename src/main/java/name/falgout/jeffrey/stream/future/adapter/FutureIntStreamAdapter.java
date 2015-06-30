package name.falgout.jeffrey.stream.future.adapter;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
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

class FutureIntStreamAdapter extends FutureBaseStreamAdapter<Integer, FutureIntStream, IntStream>
        implements FutureIntStream {
    FutureIntStreamAdapter(CompletableFuture<IntStream> delegate) {
        super(delegate);
    }

    FutureIntStreamAdapter(CompletableFuture<IntStream> delegate, Executor executor) {
        super(delegate, executor);
    }

    FutureIntStreamAdapter(CompletableFuture<IntStream> delegate, Void async) {
        super(delegate, async);
    }

    FutureIntStreamAdapter(CompletableFuture<IntStream> delegate,
            FutureBaseStreamAdapter<?, ?, ?> parent) {
        super(delegate, parent);
    }

    @Override
    protected FutureIntStream getSelf() {
        return this;
    }

    @Override
    protected FutureIntStream newStream(CompletableFuture<IntStream> delegate) {
        return new FutureIntStreamAdapter(delegate, this);
    }

    @Override
    public FutureIntStream filter(IntPredicate predicate) {
        return chain(thenApply(s -> s.filter(predicate)));
    }

    @Override
    public FutureIntStream map(IntUnaryOperator mapper) {
        return chain(thenApply(s -> s.map(mapper)));
    }

    @Override
    public <U> FutureStream<U> mapToObj(IntFunction<? extends U> mapper) {
        return new FutureStreamAdapter<>(thenApply(s -> s.mapToObj(mapper)), this);
    }

    @Override
    public FutureLongStream mapToLong(IntToLongFunction mapper) {
        return new FutureLongStreamAdapter(thenApply(s -> s.mapToLong(mapper)), this);
    }

    @Override
    public FutureDoubleStream mapToDouble(IntToDoubleFunction mapper) {
        return new FutureDoubleStreamAdapter(thenApply(s -> s.mapToDouble(mapper)), this);
    }

    @Override
    public FutureIntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return chain(thenApply(s -> s.flatMap(mapper)));
    }

    @Override
    public FutureIntStream distinct() {
        return chain(thenApply(IntStream::distinct));
    }

    @Override
    public FutureIntStream sorted() {
        return chain(thenApply(IntStream::sorted));
    }

    @Override
    public FutureIntStream peek(IntConsumer action) {
        return chain(thenApply(s -> s.peek(action)));
    }

    @Override
    public FutureIntStream limit(long maxSize) {
        return chain(thenApply(s -> s.limit(maxSize)));
    }

    @Override
    public FutureIntStream skip(long n) {
        return chain(thenApply(s -> s.skip(n)));
    }

    @Override
    public void forEach(IntConsumer action) {
        thenAccept(s -> s.forEach(action));
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        thenAccept(s -> s.forEachOrdered(action));
    }

    @Override
    public Future<int[]> toArray() {
        return thenApply(IntStream::toArray);
    }

    @Override
    public Future<Integer> reduce(int identity, IntBinaryOperator op) {
        return thenApply(s -> s.reduce(identity, op));
    }

    @Override
    public Future<OptionalInt> reduce(IntBinaryOperator op) {
        return thenApply(s -> s.reduce(op));
    }

    @Override
    public <R> Future<R> collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator,
            BiConsumer<R, R> combiner) {
        return thenApply(s -> s.collect(supplier, accumulator, combiner));
    }

    @Override
    public Future<Integer> sum() {
        return thenApply(IntStream::sum);
    }

    @Override
    public Future<OptionalInt> min() {
        return thenApply(IntStream::min);
    }

    @Override
    public Future<OptionalInt> max() {
        return thenApply(IntStream::max);
    }

    @Override
    public Future<Long> count() {
        return thenApply(IntStream::count);
    }

    @Override
    public Future<OptionalDouble> average() {
        return thenApply(IntStream::average);
    }

    @Override
    public Future<IntSummaryStatistics> summaryStatistics() {
        return thenApply(IntStream::summaryStatistics);
    }

    @Override
    public Future<Boolean> anyMatch(IntPredicate predicate) {
        return thenApply(s -> s.anyMatch(predicate));
    }

    @Override
    public Future<Boolean> allMatch(IntPredicate predicate) {
        return thenApply(s -> s.allMatch(predicate));
    }

    @Override
    public Future<Boolean> noneMatch(IntPredicate predicate) {
        return thenApply(s -> s.noneMatch(predicate));
    }

    @Override
    public Future<OptionalInt> findFirst() {
        return thenApply(IntStream::findFirst);
    }

    @Override
    public Future<OptionalInt> findAny() {
        return thenApply(IntStream::findAny);
    }

    @Override
    public FutureLongStream asLongStream() {
        return new FutureLongStreamAdapter(thenApply(IntStream::asLongStream), this);
    }

    @Override
    public FutureDoubleStream asDoubleStream() {
        return new FutureDoubleStreamAdapter(thenApply(IntStream::asDoubleStream), this);
    }

    @Override
    public FutureStream<Integer> boxed() {
        return new FutureStreamAdapter<>(thenApply(IntStream::boxed), this);
    }
}
