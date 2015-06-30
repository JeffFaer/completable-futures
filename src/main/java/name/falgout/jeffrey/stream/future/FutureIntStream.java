package name.falgout.jeffrey.stream.future;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
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

public interface FutureIntStream extends FutureBaseStream<Integer, FutureIntStream> {

    public FutureIntStream filter(IntPredicate predicate);

    public FutureIntStream map(IntUnaryOperator mapper);

    public <U> FutureStream<U> mapToObj(IntFunction<? extends U> mapper);

    public FutureLongStream mapToLong(IntToLongFunction mapper);

    public FutureDoubleStream mapToDouble(IntToDoubleFunction mapper);

    public FutureIntStream flatMap(IntFunction<? extends IntStream> mapper);

    public FutureIntStream distinct();

    public FutureIntStream sorted();

    public FutureIntStream peek(IntConsumer action);

    public FutureIntStream limit(long maxSize);

    public FutureIntStream skip(long n);

    public void forEach(IntConsumer action);

    public void forEachOrdered(IntConsumer action);

    public Future<int[]> toArray();

    public Future<Integer> reduce(int identity, IntBinaryOperator op);

    public Future<OptionalInt> reduce(IntBinaryOperator op);

    public <R> Future<R> collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator,
            BiConsumer<R, R> combiner);

    public Future<Integer> sum();

    public Future<OptionalInt> min();

    public Future<OptionalInt> max();

    public Future<Long> count();

    public Future<OptionalDouble> average();

    public Future<IntSummaryStatistics> summaryStatistics();

    public Future<Boolean> anyMatch(IntPredicate predicate);

    public Future<Boolean> allMatch(IntPredicate predicate);

    public Future<Boolean> noneMatch(IntPredicate predicate);

    public Future<OptionalInt> findFirst();

    public Future<OptionalInt> findAny();

    public FutureLongStream asLongStream();

    public FutureDoubleStream asDoubleStream();

    public FutureStream<Integer> boxed();
}
