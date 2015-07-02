package name.falgout.jeffrey.stream.future;

import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.concurrent.Future;
import java.util.function.LongFunction;

import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingLongBinaryOperator;
import throwing.function.ThrowingLongConsumer;
import throwing.function.ThrowingLongFunction;
import throwing.function.ThrowingLongPredicate;
import throwing.function.ThrowingObjLongConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.ThrowingLongStreamIntermediate;

public interface FutureLongStream extends
    FutureBaseStream<Long, FutureLongStream>,
    ThrowingLongStreamIntermediate<Throwable, FutureIntStream, FutureLongStream, FutureDoubleStream> {
  @Override
  default public <U> FutureStream<U> normalMapToObj(LongFunction<? extends U> mapper) {
    return mapToObj(mapper::apply);
  }

  @Override
  public <U> FutureStream<U> mapToObj(ThrowingLongFunction<? extends U, ? extends Throwable> mapper);

  @Override
  public FutureStream<Long> boxed();

  public Future<Void> forEach(ThrowingLongConsumer<?> action);

  public Future<Void> forEachOrdered(ThrowingLongConsumer<?> action);

  public Future<long[]> toArray();

  public Future<Long> reduce(long identity, ThrowingLongBinaryOperator<?> op);

  public Future<OptionalLong> reduce(ThrowingLongBinaryOperator<?> op);

  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingObjLongConsumer<R, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner);

  public Future<Long> sum();

  public Future<OptionalLong> min();

  public Future<OptionalLong> max();

  public Future<Long> count();

  public Future<OptionalDouble> average();

  public Future<LongSummaryStatistics> summaryStatistics();

  public Future<Boolean> anyMatch(ThrowingLongPredicate<?> predicate);

  public Future<Boolean> allMatch(ThrowingLongPredicate<?> predicate);

  public Future<Boolean> noneMatch(ThrowingLongPredicate<?> predicate);

  public Future<OptionalLong> findFirst();

  public Future<OptionalLong> findAny();
}
