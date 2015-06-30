package name.falgout.jeffrey.stream.future;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
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

public interface FutureLongStream extends FutureBaseStream<Long, FutureLongStream> {

    public FutureLongStream filter(LongPredicate predicate);

    public FutureLongStream map(LongUnaryOperator mapper);

    public <U> FutureStream<U> mapToObj(LongFunction<? extends U> mapper);

    public FutureIntStream mapToInt(LongToIntFunction mapper);

    public FutureDoubleStream mapToDouble(LongToDoubleFunction mapper);

    public FutureLongStream flatMap(LongFunction<? extends LongStream> mapper);

    public FutureLongStream distinct();

    public FutureLongStream sorted();

    public FutureLongStream peek(LongConsumer action);

    public FutureLongStream limit(long maxSize);

    public FutureLongStream skip(long n);

    public void forEach(LongConsumer action);

    public void forEachOrdered(LongConsumer action);

    public Future<long[]> toArray();

    public Future<Long> reduce(long identity, LongBinaryOperator op);

    public Future<OptionalLong> reduce(LongBinaryOperator op);

    public <R> Future<R> collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator,
            BiConsumer<R, R> combiner);

    public Future<Long> sum();

    public Future<OptionalLong> min();

    public Future<OptionalLong> max();

    public Future<Long> count();

    public Future<OptionalDouble> average();

    public Future<LongSummaryStatistics> summaryStatistics();

    public Future<Boolean> anyMatch(LongPredicate predicate);

    public Future<Boolean> allMatch(LongPredicate predicate);

    public Future<Boolean> noneMatch(LongPredicate predicate);

    public Future<OptionalLong> findFirst();

    public Future<OptionalLong> findAny();

    public FutureDoubleStream asDoubleStream();

    public FutureStream<Long> boxed();
}
