package name.falgout.jeffrey.stream.future;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;

import throwing.ThrowingComparator;
import throwing.function.ThrowingBiConsumer;
import throwing.function.ThrowingBiFunction;
import throwing.function.ThrowingBinaryOperator;
import throwing.function.ThrowingConsumer;
import throwing.function.ThrowingFunction;
import throwing.function.ThrowingPredicate;
import throwing.function.ThrowingSupplier;
import throwing.stream.ThrowingCollector;
import throwing.stream.ThrowingStream;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.intermediate.ThrowingStreamIntermediate;

public interface FutureStream<T> extends
    FutureBaseStream<T, FutureStream<T>>,
    ThrowingStreamIntermediate<T, Throwable, FutureStream<T>, FutureIntStream, FutureLongStream, FutureDoubleStream> {
  @Override
  default public <R> FutureStream<R> normalMap(Function<? super T, ? extends R> mapper) {
    return map(mapper::apply);
  }

  @Override
  public <R> FutureStream<R> map(
      ThrowingFunction<? super T, ? extends R, ? extends Throwable> mapper);

  @Override
  default public <R> FutureStream<R> normalFlatMap(
      Function<? super T, ? extends ThrowingStream<? extends R, ? extends Throwable>> mapper) {
    return flatMap(mapper::apply);
  }

  @Override
  public <R> FutureStream<R> flatMap(
      ThrowingFunction<? super T, ? extends ThrowingStream<? extends R, ? extends Throwable>, ? extends Throwable> mapper);

  public Future<Void> forEach(ThrowingConsumer<? super T, ?> action);

  public Future<Void> forEachOrdered(ThrowingConsumer<? super T, ?> action);

  public Future<Object[]> toArray();

  public <A> Future<A[]> toArray(IntFunction<A[]> generator);

  public Future<T> reduce(T identity, ThrowingBinaryOperator<T, ?> accumulator);

  public Future<Optional<T>> reduce(ThrowingBinaryOperator<T, ?> accumulator);

  public <U> Future<U> reduce(U identity, ThrowingBiFunction<U, ? super T, U, ?> accumulator,
      ThrowingBinaryOperator<U, ?> combiner);

  public <R> Future<R> collect(ThrowingSupplier<R, ?> supplier,
      ThrowingBiConsumer<R, ? super T, ?> accumulator, ThrowingBiConsumer<R, R, ?> combiner);

  default public <R, A> Future<R> collect(Collector<? super T, A, R> collector) {
    return collect(ThrowingBridge.of(collector));
  }

  public <R, A> Future<R> collect(ThrowingCollector<? super T, A, R, ?> collector);

  public Future<Optional<T>> min(ThrowingComparator<? super T, ?> comparator);

  public Future<Optional<T>> max(ThrowingComparator<? super T, ?> comparator);

  public Future<Long> count();

  public Future<Boolean> anyMatch(ThrowingPredicate<? super T, ?> predicate);

  public Future<Boolean> allMatch(ThrowingPredicate<? super T, ?> predicate);

  public Future<Boolean> noneMatch(ThrowingPredicate<? super T, ?> predicate);

  public Future<Optional<T>> findFirst();

  public Future<Optional<T>> findAny();
}
