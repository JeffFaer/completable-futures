package name.falgout.jeffrey.stream.future.adapter;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
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

class FutureDoubleStreamAdapter extends FutureBaseStreamAdapter<Double, FutureDoubleStream, DoubleStream>
        implements FutureDoubleStream {
    FutureDoubleStreamAdapter(CompletableFuture<DoubleStream> delegate) {
        super(delegate);
    }

    FutureDoubleStreamAdapter(CompletableFuture<DoubleStream> delegate, Executor executor) {
        super(delegate, executor);
    }

    FutureDoubleStreamAdapter(CompletableFuture<DoubleStream> delegate, Void async) {
        super(delegate, async);
    }

    FutureDoubleStreamAdapter(CompletableFuture<DoubleStream> delegate,
            FutureBaseStreamAdapter<?, ?, ?> parent) {
        super(delegate, parent);
    }

    @Override
    protected FutureDoubleStream getSelf() {
        return this;
    }

    @Override
    protected FutureDoubleStream newStream(CompletableFuture<DoubleStream> delegate) {
        return new FutureDoubleStreamAdapter(delegate, this);
    }

    @Override
    public FutureDoubleStream filter(DoublePredicate predicate) {
        return chain(thenApply(s -> s.filter(predicate)));
    }

    @Override
    public FutureDoubleStream map(DoubleUnaryOperator mapper) {
        return chain(thenApply(s -> s.map(mapper)));
    }

    @Override
    public <U> FutureStream<U> mapToObj(DoubleFunction<? extends U> mapper) {
        return new FutureStreamAdapter<>(thenApply(s -> s.mapToObj(mapper)), this);
    }

    @Override
    public FutureIntStream mapToInt(DoubleToIntFunction mapper) {
        return new FutureIntStreamAdapter(thenApply(s -> s.mapToInt(mapper)), this);
    }

    @Override
    public FutureLongStream mapToLong(DoubleToLongFunction mapper) {
        return new FutureLongStreamAdapter(thenApply(s -> s.mapToLong(mapper)), this);
    }

    @Override
    public FutureDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
        return chain(thenApply(s -> s.flatMap(mapper)));
    }

    @Override
    public FutureDoubleStream distinct() {
        return chain(thenApply(DoubleStream::distinct));
    }

    @Override
    public FutureDoubleStream sorted() {
        return chain(thenApply(DoubleStream::sorted));
    }

    @Override
    public FutureDoubleStream peek(DoubleConsumer action) {
        return chain(thenApply(s -> s.peek(action)));
    }

    @Override
    public FutureDoubleStream limit(long maxSize) {
        return chain(thenApply(s -> s.limit(maxSize)));
    }

    @Override
    public FutureDoubleStream skip(long n) {
        return chain(thenApply(s -> s.skip(n)));
    }

    @Override
    public void forEach(DoubleConsumer action) {
        thenAccept(s -> s.forEach(action));
    }

    @Override
    public void forEachOrdered(DoubleConsumer action) {
        thenAccept(s -> s.forEachOrdered(action));
    }

    @Override
    public Future<double[]> toArray() {
        return thenApply(DoubleStream::toArray);
    }

    @Override
    public Future<Double> reduce(double identity, DoubleBinaryOperator op) {
        return thenApply(s -> s.reduce(identity, op));
    }

    @Override
    public Future<OptionalDouble> reduce(DoubleBinaryOperator op) {
        return thenApply(s -> s.reduce(op));
    }

    @Override
    public <R> Future<R> collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator,
            BiConsumer<R, R> combiner) {
        return thenApply(s -> s.collect(supplier, accumulator, combiner));
    }

    @Override
    public Future<Double> sum() {
        return thenApply(DoubleStream::sum);
    }

    @Override
    public Future<OptionalDouble> min() {
        return thenApply(DoubleStream::min);
    }

    @Override
    public Future<OptionalDouble> max() {
        return thenApply(DoubleStream::max);
    }

    @Override
    public Future<Long> count() {
        return thenApply(DoubleStream::count);
    }

    @Override
    public Future<OptionalDouble> average() {
        return thenApply(DoubleStream::average);
    }

    @Override
    public Future<DoubleSummaryStatistics> summaryStatistics() {
        return thenApply(DoubleStream::summaryStatistics);
    }

    @Override
    public Future<Boolean> anyMatch(DoublePredicate predicate) {
        return thenApply(s -> s.anyMatch(predicate));
    }

    @Override
    public Future<Boolean> allMatch(DoublePredicate predicate) {
        return thenApply(s -> s.allMatch(predicate));
    }

    @Override
    public Future<Boolean> noneMatch(DoublePredicate predicate) {
        return thenApply(s -> s.noneMatch(predicate));
    }

    @Override
    public Future<OptionalDouble> findFirst() {
        return thenApply(DoubleStream::findFirst);
    }

    @Override
    public Future<OptionalDouble> findAny() {
        return thenApply(DoubleStream::findAny);
    }

    @Override
    public FutureStream<Double> boxed() {
        return new FutureStreamAdapter<>(thenApply(DoubleStream::boxed), this);
    }
}
