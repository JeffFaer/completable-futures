package name.falgout.jeffrey.stream.future;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.concurrent.Future;
import java.util.function.DoubleFunction;

import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingDoubleBinaryOperator;
import throwing.function.ThrowingDoubleConsumer;
import throwing.function.ThrowingDoubleFunction;
import throwing.function.ThrowingDoublePredicate;
import throwing.function.ThrowingObjDoubleConsumer;
import throwing.function.ThrowingSupplier;
import throwing.stream.intermediate.ThrowingDoubleStreamIntermediate;

public interface FutureDoubleStream extends
    FutureBaseStream<Double, FutureDoubleStream>,
    ThrowingDoubleStreamIntermediate<Throwable, FutureIntStream, FutureLongStream, FutureDoubleStream> {
  @Override
  default public <U> FutureStream<U> normalMapToObj(DoubleFunction<? extends U> mapper) {
    return mapToObj(mapper::apply);
  }

  @Override
  public <U> FutureStream<U> mapToObj(
      ThrowingDoubleFunction<? extends U, ? extends Throwable> mapper);

  @Override
  public FutureStream<Double> boxed();

  public Future<Void> forEach(ThrowingDoubleConsumer<?> action);

  public Future<Void> forEachOrdered(ThrowingDoubleConsumer<?> action);

  public Future<double[]> toArray();

  public Future<Double> reduce(double identity, ThrowingDoubleBinaryOperator<?> op);

  public Future<OptionalDouble> reduce(ThrowingDoubleBinaryOperator<?> op);

  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingObjDoubleConsumer<R, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner);

  public Future<Double> sum();

  public Future<OptionalDouble> min();

  public Future<OptionalDouble> max();

  public Future<Long> count();

  public Future<OptionalDouble> average();

  public Future<DoubleSummaryStatistics> summaryStatistics();

  public Future<Boolean> anyMatch(ThrowingDoublePredicate<?> predicate);

  public Future<Boolean> allMatch(ThrowingDoublePredicate<?> predicate);

  public Future<Boolean> noneMatch(ThrowingDoublePredicate<?> predicate);

  public Future<OptionalDouble> findFirst();

  public Future<OptionalDouble> findAny();
}
