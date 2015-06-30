package name.falgout.jeffrey.stream.future;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
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

public interface FutureDoubleStream extends FutureBaseStream<Double, FutureDoubleStream> {
    public FutureDoubleStream filter(DoublePredicate predicate);

    public FutureDoubleStream map(DoubleUnaryOperator mapper);

    public <U> FutureStream<U> mapToObj(DoubleFunction<? extends U> mapper);

    public FutureIntStream mapToInt(DoubleToIntFunction mapper);

    public FutureLongStream mapToLong(DoubleToLongFunction mapper);

    public FutureDoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper);

    public FutureDoubleStream distinct();

    public FutureDoubleStream sorted();

    public FutureDoubleStream peek(DoubleConsumer action);

    public FutureDoubleStream limit(long maxSize);

    public FutureDoubleStream skip(long n);

    public void forEach(DoubleConsumer action);

    public void forEachOrdered(DoubleConsumer action);

    public Future<double[]> toArray();

    public Future<Double> reduce(double identity, DoubleBinaryOperator op);

    public Future<OptionalDouble> reduce(DoubleBinaryOperator op);

    public <R> Future<R> collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator,
            BiConsumer<R, R> combiner);

    public Future<Double> sum();

    public Future<OptionalDouble> min();

    public Future<OptionalDouble> max();

    public Future<Long> count();

    public Future<OptionalDouble> average();

    public Future<DoubleSummaryStatistics> summaryStatistics();

    public Future<Boolean> anyMatch(DoublePredicate predicate);

    public Future<Boolean> allMatch(DoublePredicate predicate);

    public Future<Boolean> noneMatch(DoublePredicate predicate);

    public Future<OptionalDouble> findFirst();

    public Future<OptionalDouble> findAny();

    public FutureStream<Double> boxed();
}
