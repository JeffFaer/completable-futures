package name.falgout.jeffrey.stream.future.adapter;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
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

class FutureLongStreamAdapter extends FutureBaseStreamAdapter<Long, FutureLongStream, LongStream>
        implements FutureLongStream {
    FutureLongStreamAdapter(CompletableFuture<LongStream> delegate) {
        super(delegate);
    }

    FutureLongStreamAdapter(CompletableFuture<LongStream> delegate, Executor executor) {
        super(delegate, executor);
    }

    FutureLongStreamAdapter(CompletableFuture<LongStream> delegate, Void async) {
        super(delegate, async);
    }

    FutureLongStreamAdapter(CompletableFuture<LongStream> delegate,
            FutureBaseStreamAdapter<?, ?, ?> parent) {
        super(delegate, parent);
    }

    @Override
    protected FutureLongStream getSelf() {
        return this;
    }

    @Override
    protected FutureLongStream newStream(CompletableFuture<LongStream> delegate) {
        return new FutureLongStreamAdapter(delegate, this);
    }

    @Override
    public FutureLongStream filter(LongPredicate predicate) {
        return chain(thenApply(s -> s.filter(predicate)));
    }

    @Override
    public FutureLongStream map(LongUnaryOperator mapper) {
        return chain(thenApply(s -> s.map(mapper)));
    }

    @Override
    public <U> FutureStream<U> mapToObj(LongFunction<? extends U> mapper) {
        return new FutureStreamAdapter<>(thenApply(s -> s.mapToObj(mapper)), this);
    }

    @Override
    public FutureIntStream mapToInt(LongToIntFunction mapper) {
        return new FutureIntStreamAdapter(thenApply(s -> s.mapToInt(mapper)), this);
    }

    @Override
    public FutureDoubleStream mapToDouble(LongToDoubleFunction mapper) {
        return new FutureDoubleStreamAdapter(thenApply(s -> s.mapToDouble(mapper)), this);
    }

    @Override
    public FutureLongStream flatMap(LongFunction<? extends LongStream> mapper) {
        return chain(thenApply(s -> s.flatMap(mapper)));
    }

    @Override
    public FutureLongStream distinct() {
        return chain(thenApply(LongStream::distinct));
    }

    @Override
    public FutureLongStream sorted() {
        return chain(thenApply(LongStream::sorted));
    }

    @Override
    public FutureLongStream peek(LongConsumer action) {
        return chain(thenApply(s -> s.peek(action)));
    }

    @Override
    public FutureLongStream limit(long maxSize) {
        return chain(thenApply(s -> s.limit(maxSize)));
    }

    @Override
    public FutureLongStream skip(long n) {
        return chain(thenApply(s -> s.skip(n)));
    }

    @Override
    public void forEach(LongConsumer action) {
        thenAccept(s -> s.forEach(action));
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        thenAccept(s -> s.forEachOrdered(action));
    }

    @Override
    public Future<long[]> toArray() {
        return thenApply(LongStream::toArray);
    }

    @Override
    public Future<Long> reduce(long identity, LongBinaryOperator op) {
        return thenApply(s -> s.reduce(identity, op));
    }

    @Override
    public Future<OptionalLong> reduce(LongBinaryOperator op) {
        return thenApply(s -> s.reduce(op));
    }

    @Override
    public <R> Future<R> collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator,
            BiConsumer<R, R> combiner) {
        return thenApply(s -> s.collect(supplier, accumulator, combiner));
    }

    @Override
    public Future<Long> sum() {
        return thenApply(LongStream::sum);
    }

    @Override
    public Future<OptionalLong> min() {
        return thenApply(LongStream::min);
    }

    @Override
    public Future<OptionalLong> max() {
        return thenApply(LongStream::max);
    }

    @Override
    public Future<Long> count() {
        return thenApply(LongStream::count);
    }

    @Override
    public Future<OptionalDouble> average() {
        return thenApply(LongStream::average);
    }

    @Override
    public Future<LongSummaryStatistics> summaryStatistics() {
        return thenApply(LongStream::summaryStatistics);
    }

    @Override
    public Future<Boolean> anyMatch(LongPredicate predicate) {
        return thenApply(s -> s.anyMatch(predicate));
    }

    @Override
    public Future<Boolean> allMatch(LongPredicate predicate) {
        return thenApply(s -> s.allMatch(predicate));
    }

    @Override
    public Future<Boolean> noneMatch(LongPredicate predicate) {
        return thenApply(s -> s.noneMatch(predicate));
    }

    @Override
    public Future<OptionalLong> findFirst() {
        return thenApply(LongStream::findFirst);
    }

    @Override
    public Future<OptionalLong> findAny() {
        return thenApply(LongStream::findAny);
    }

    @Override
    public FutureDoubleStream asDoubleStream() {
        return new FutureDoubleStreamAdapter(thenApply(LongStream::asDoubleStream), this);
    }

    @Override
    public FutureStream<Long> boxed() {
        return new FutureStreamAdapter<>(thenApply(LongStream::boxed), this);
    }
}
