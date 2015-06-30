package name.falgout.jeffrey.stream.future;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface FutureStream<T> extends FutureBaseStream<T, FutureStream<T>> {
    public FutureStream<T> filter(Predicate<? super T> predicate);

    public <R> FutureStream<R> map(Function<? super T, ? extends R> mapper);

    public FutureIntStream mapToInt(ToIntFunction<? super T> mapper);

    public FutureLongStream mapToLong(ToLongFunction<? super T> mapper);

    public FutureDoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

    public <R> FutureStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    public FutureIntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

    public FutureLongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

    public FutureDoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

    public FutureStream<T> distinct();

    public FutureStream<T> sorted();

    public FutureStream<T> sorted(Comparator<? super T> comparator);

    public FutureStream<T> peek(Consumer<? super T> action);

    public FutureStream<T> limit(long maxSize);

    public FutureStream<T> skip(long n);

    public void forEach(Consumer<? super T> action);

    public void forEachOrdered(Consumer<? super T> action);

    public Future<Object[]> toArray();

    public <A> Future<A[]> toArray(IntFunction<A[]> generator);

    public Future<T> reduce(T identity, BinaryOperator<T> accumulator);

    public Future<Optional<T>> reduce(BinaryOperator<T> accumulator);

    public <U> Future<U> reduce(U identity, BiFunction<U, ? super T, U> accumulator,
            BinaryOperator<U> combiner);

    public <R> Future<R> collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator,
            BiConsumer<R, R> combiner);

    public <R, A> Future<R> collect(Collector<? super T, A, R> collector);

    public Future<Optional<T>> min(Comparator<? super T> comparator);

    public Future<Optional<T>> max(Comparator<? super T> comparator);

    public Future<Long> count();

    public Future<Boolean> anyMatch(Predicate<? super T> predicate);

    public Future<Boolean> allMatch(Predicate<? super T> predicate);

    public Future<Boolean> noneMatch(Predicate<? super T> predicate);

    public Future<Optional<T>> findFirst();

    public Future<Optional<T>> findAny();
}
