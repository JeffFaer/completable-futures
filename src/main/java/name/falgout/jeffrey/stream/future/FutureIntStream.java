package name.falgout.jeffrey.stream.future;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.Future;
import java.util.function.IntFunction;

import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingIntBinaryOperator;
import throwing.function.ThrowingIntConsumer;
import throwing.function.ThrowingIntFunction;
import throwing.function.ThrowingIntPredicate;
import throwing.function.ThrowingObjIntConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.ThrowingIntStreamIntermediate;

public interface FutureIntStream extends
    FutureBaseStream<Integer, FutureIntStream>,
    ThrowingIntStreamIntermediate<Throwable, FutureIntStream, FutureLongStream, FutureDoubleStream> {
  @Override
  default public <U> FutureStream<U> normalMapToObj(IntFunction<? extends U> mapper) {
    return mapToObj(mapper::apply);
  }

  @Override
  public <U> FutureStream<U> mapToObj(ThrowingIntFunction<? extends U, ? extends Throwable> mapper);

  @Override
  public FutureStream<Integer> boxed();

  public Future<Void> forEach(ThrowingIntConsumer<?> action);

  public Future<Void> forEachOrdered(ThrowingIntConsumer<?> action);

  public Future<int[]> toArray();

  public Future<Integer> reduce(int identity, ThrowingIntBinaryOperator<?> op);

  public Future<OptionalInt> reduce(ThrowingIntBinaryOperator<?> op);

  public <R> Future<R> collect(ThrowingSupplier<R,?> supplier, ThrowingObjIntConsumer<R,?> accumulator,
      ThrowingBiConsumer<R, R,?> combiner);

  public Future<Integer> sum();

  public Future<OptionalInt> min();

  public Future<OptionalInt> max();

  public Future<Long> count();

  public Future<OptionalDouble> average();

  public Future<IntSummaryStatistics> summaryStatistics();

  public Future<Boolean> anyMatch(ThrowingIntPredicate<?> predicate);

  public Future<Boolean> allMatch(ThrowingIntPredicate<?> predicate);

  public Future<Boolean> noneMatch(ThrowingIntPredicate<?> predicate);

  public Future<OptionalInt> findFirst();

  public Future<OptionalInt> findAny();
}
