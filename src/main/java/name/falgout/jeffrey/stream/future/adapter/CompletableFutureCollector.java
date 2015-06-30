package name.falgout.jeffrey.stream.future.adapter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CompletableFutureCollector<T, A, R> extends
    CompletableFutureAdapter<Collector<T, A, R>> implements
    Collector<CompletableFuture<T>, AtomicReference<CompletableFuture<A>>, CompletableFuture<R>> {
  CompletableFutureCollector(Collector<T, A, R> delegate, Executor executor) {
    super(delegate, executor);
  }

  CompletableFutureCollector(Collector<T, A, R> delegate, Void async) {
    super(delegate, async);
  }

  CompletableFutureCollector(Collector<T, A, R> delegate) {
    super(delegate);
  }

  @Override
  public Supplier<AtomicReference<CompletableFuture<A>>> supplier() {
    return () -> new AtomicReference<>(CompletableFuture.completedFuture(getDelegate().supplier()
        .get()));
  }

  @Override
  public BiConsumer<AtomicReference<CompletableFuture<A>>, CompletableFuture<T>> accumulator() {
    return (ref, future) -> {
      while (true) {
        CompletableFuture<A> a = ref.get();
        CompletableFuture<A> newA = thenAcceptBoth(a, future, getDelegate().accumulator()).thenApply(
            v -> a.join());

        if (ref.compareAndSet(a, newA)) {
          return;
        } else {
          newA.cancel(false);
        }
      }
    };
  }

  @Override
  public BinaryOperator<AtomicReference<CompletableFuture<A>>> combiner() {
    return (ref1, ref2) -> {
      CompletableFuture<A> a1 = ref1.get();
      CompletableFuture<A> a2 = ref2.get();
      return new AtomicReference<>(thenCombine(a1, a2, getDelegate().combiner()));
    };
  }

  @Override
  public Function<AtomicReference<CompletableFuture<A>>, CompletableFuture<R>> finisher() {
    return ref -> {
      return thenApply(
          ref.get(),
          getDelegate().characteristics().contains(Collector.Characteristics.IDENTITY_FINISH) ? unsafeCast()
              : getDelegate().finisher());
    };
  }

  @SuppressWarnings("unchecked")
  private static <R> Function<Object, R> unsafeCast() {
    return o -> (R) o;
  }

  @Override
  public Set<Collector.Characteristics> characteristics() {
    Set<Collector.Characteristics> characteristics = new LinkedHashSet<>(
        getDelegate().characteristics());
    characteristics.remove(Collector.Characteristics.IDENTITY_FINISH);

    return characteristics;
  }
}
