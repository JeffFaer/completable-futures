package name.falgout.jeffrey.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import name.falgout.jeffrey.stream.future.FutureStream;
import name.falgout.jeffrey.stream.future.adapter.FutureStreamBridge;
import throwing.function.ThrowingSupplier;

public final class CompletableFutures {
  private CompletableFutures() {}

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future) {
    return newCompletableFuture(future,
        (Function<Runnable, CompletableFuture<Void>>) CompletableFuture::runAsync);
  }

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future, Executor executor) {
    return newCompletableFuture(future,
        (Function<Runnable, CompletableFuture<Void>>) r -> CompletableFuture.runAsync(r, executor));
  }

  private static <T> CompletableFuture<T> newCompletableFuture(Future<T> future,
      Function<Runnable, CompletableFuture<Void>> executor) {
    return future instanceof CompletableFuture ? (CompletableFuture<T>) future
        : newCompletableFuture(() -> {
          try {
            return future.get();
          } catch (ExecutionException e) {
            throw e.getCause();
          }
        }, executor);
  }

  public static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier) {
    return newCompletableFuture(supplier,
        (Function<Runnable, CompletableFuture<Void>>) CompletableFuture::runAsync);
  }

  public static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier,
      Executor executor) {
    return newCompletableFuture(supplier,
        (Function<Runnable, CompletableFuture<Void>>) r -> CompletableFuture.runAsync(r, executor));
  }

  private static <T> CompletableFuture<T> newCompletableFuture(ThrowingSupplier<T, ?> supplier,
      Function<Runnable, CompletableFuture<Void>> executor) {
    CompletableFuture<T> future = new CompletableFuture<>();
    executor.apply(() -> {
      try {
        future.complete(supplier.get());
      } catch (Throwable t) {
        future.completeExceptionally(t);
      }
    });
    return future;
  }

  public static <T> CompletableFuture<T> failed(Throwable cause) {
    CompletableFuture<T> f = new CompletableFuture<>();
    f.completeExceptionally(cause);
    return f;
  }

  public static <T, A, R> Collector<Future<T>, ?, ? extends Future<R>> collector(
      Collector<T, A, R> collector) {
    return collector(collector, FutureStreamBridge::collector,
        CompletableFutures::newCompletableFuture);
  }

  public static <T, A, R> Collector<Future<T>, ?, ? extends Future<R>> collectorAsync(
      Collector<T, A, R> collector) {
    return collector(collector, FutureStreamBridge::collectorAsync,
        CompletableFutures::newCompletableFuture);
  }

  public static <T, A, R> Collector<Future<T>, ?, ? extends Future<R>> collectorAsync(
      Collector<T, A, R> collector, Executor executor) {
    return collector(collector, c -> FutureStreamBridge.collectorAsync(c, executor),
        f -> CompletableFutures.newCompletableFuture(f, executor));
  }

  private static <T, A, R> Collector<Future<T>, ?, ? extends Future<R>> collector(
      Collector<T, A, R> collector,
      Function<Collector<T, A, R>, Collector<CompletableFuture<T>, ?, CompletableFuture<R>>> futurify,
      Function<Future<T>, CompletableFuture<T>> mapInput) {
    Collector<CompletableFuture<T>, ?, CompletableFuture<R>> futureCollector = futurify.apply(collector);
    Collector<Future<T>, ?, CompletableFuture<R>> mapped = Collectors.mapping(mapInput,
        futureCollector);
    return mapped;
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<List<T>>> toList() {
    return collector(Collectors.toList());
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<List<T>>> toListAsync() {
    return collectorAsync(Collectors.toList());
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<List<T>>> toListAsync(Executor executor) {
    return collectorAsync(Collectors.toList(), executor);
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<Set<T>>> toSet() {
    return collector(Collectors.toSet());
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<Set<T>>> toSetAsync() {
    return collectorAsync(Collectors.toSet());
  }

  public static <T> Collector<Future<T>, ?, ? extends Future<Set<T>>> toSetAsync(Executor executor) {
    return collectorAsync(Collectors.toSet(), executor);
  }

  public static <T, C extends Collection<T>> Collector<Future<T>, ?, ? extends Future<C>> toCollection(
      Supplier<C> supplier) {
    return collector(Collectors.toCollection(supplier));
  }

  public static <T, C extends Collection<T>> Collector<Future<T>, ?, ? extends Future<C>> toCollectionAsync(
      Supplier<C> supplier) {
    return collectorAsync(Collectors.toCollection(supplier));
  }

  public static <T, C extends Collection<T>> Collector<Future<T>, ?, ? extends Future<C>> toCollectionAsync(
      Supplier<C> supplier, Executor executor) {
    return collectorAsync(Collectors.toCollection(supplier), executor);
  }

  @SafeVarargs
  public static <T> Future<List<T>> allOf(Future<T>... futures) {
    return allOf(Arrays.asList(futures));
  }

  public static <T> Future<List<T>> allOf(Iterable<? extends Future<T>> futures) {
    return allOf(futures, toList());
  }

  private static <T> Future<List<T>> allOf(Iterable<? extends Future<T>> futures,
      Collector<Future<T>, ?, ? extends Future<List<T>>> collector) {
    return StreamSupport.stream(futures.spliterator(), false).collect(collector);
  }

  @SafeVarargs
  public static <T> Future<List<T>> allOfAsync(Future<T>... futures) {
    return allOfAsync(Arrays.asList(futures));
  }

  public static <T> Future<List<T>> allOfAsync(Iterable<? extends Future<T>> futures) {
    return allOf(futures, toListAsync());
  }

  @SafeVarargs
  public static <T> Future<List<T>> allOfAsync(Executor executor, Future<T>... futures) {
    return allOfAsync(Arrays.asList(futures), executor);
  }

  public static <T> Future<List<T>> allOfAsync(Iterable<? extends Future<T>> futures,
      Executor executor) {
    return allOf(futures, toListAsync(executor));
  }

  @SafeVarargs
  public static <T> FutureStream<T> stream(Future<T>... futures) {
    return stream(Arrays.asList(futures));
  }

  public static <T> FutureStream<T> stream(Iterable<? extends Future<T>> futures) {
    return FutureStreamBridge.of(createStream(futures));
  }

  private static <T> Stream<T> createStream(Iterable<T> elements) {
    return StreamSupport.stream(elements.spliterator(), false);
  }

  @SafeVarargs
  public static <T> FutureStream<T> stream(Executor executor, Future<T>... futures) {
    return stream(Arrays.asList(futures), executor);
  }

  public static <T> FutureStream<T> stream(Iterable<? extends Future<T>> futures, Executor executor) {
    return FutureStreamBridge.of(createStream(futures), executor);
  }
}
