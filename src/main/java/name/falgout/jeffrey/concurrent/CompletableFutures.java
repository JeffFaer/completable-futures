package name.falgout.jeffrey.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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

public final class CompletableFutures {
  private CompletableFutures() {}

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future) {
    return newCompletableFuture(future, CompletableFuture::supplyAsync);
  }

  public static <T> CompletableFuture<T> newCompletableFuture(Future<T> future, Executor executor) {
    return newCompletableFuture(future,
        (Supplier<T> s) -> CompletableFuture.supplyAsync(s, executor));
  }

  private static <T> CompletableFuture<T> newCompletableFuture(Future<T> future,
      Function<Supplier<T>, CompletableFuture<T>> mapper) {
    return future instanceof CompletableFuture ? (CompletableFuture<T>) future
        : mapper.apply(asSupplier(future));
  }

  private static <T> Supplier<T> asSupplier(Future<T> future) {
    return () -> {
      try {
        return future.get();
      } catch (CancellationException | InterruptedException e) {
        throw new CompletionException(e);
      } catch (ExecutionException e) {
        throw new CompletionException(e.getCause());
      }
    };
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
  public static <T> Future<List<T>> allElements(Future<T>... futures) {
    return allElements(Arrays.asList(futures));
  }

  public static <T> Future<List<T>> allElements(Iterable<Future<T>> futures) {
    return allElements(futures, toList());
  }

  private static <T> Future<List<T>> allElements(Iterable<Future<T>> futures,
      Collector<Future<T>, ?, ? extends Future<List<T>>> collector) {
    return StreamSupport.stream(futures.spliterator(), false).collect(collector);
  }

  @SafeVarargs
  public static <T> Future<List<T>> allElementsAsync(Future<T>... futures) {
    return allElementsAsync(Arrays.asList(futures));
  }

  public static <T> Future<List<T>> allElementsAsync(Iterable<Future<T>> futures) {
    return allElements(futures, toListAsync());
  }

  @SafeVarargs
  public static <T> Future<List<T>> allElementsAsync(Executor executor, Future<T>... futures) {
    return allElementsAsync(Arrays.asList(futures), executor);
  }

  public static <T> Future<List<T>> allElementsAsync(Iterable<Future<T>> futures, Executor executor) {
    return allElements(futures, toListAsync(executor));
  }

  @SafeVarargs
  public static <T> FutureStream<T> stream(Future<T>... futures) {
    return stream(Arrays.asList(futures));
  }

  public static <T> FutureStream<T> stream(Iterable<Future<T>> futures) {
    return stream(futures, CompletableFutures::allElements,
        CompletableFutures::newCompletableFuture, FutureStreamBridge::of);
  }

  private static <T> FutureStream<T> stream(Iterable<Future<T>> futures,
      Function<Iterable<Future<T>>, Future<List<T>>> listMapper,
      Function<Future<List<T>>, CompletableFuture<List<T>>> futureMapper,
      Function<CompletableFuture<Stream<T>>, FutureStream<T>> futureStreamMapper) {
    CompletableFuture<List<T>> list = listMapper.andThen(futureMapper).apply(futures);
    return futureStreamMapper.apply(list.thenApply(List::stream));
  }

  @SafeVarargs
  public static <T> FutureStream<T> streamAsync(Future<T>... futures) {
    return streamAsync(Arrays.asList(futures));
  }

  public static <T> FutureStream<T> streamAsync(Iterable<Future<T>> futures) {
    return stream(futures, CompletableFutures::allElementsAsync,
        CompletableFutures::newCompletableFuture, FutureStreamBridge::ofAsync);
  }

  @SafeVarargs
  public static <T> FutureStream<T> streamAsync(Executor executor, Future<T>... futures) {
    return streamAsync(Arrays.asList(futures), executor);
  }

  public static <T> FutureStream<T> streamAsync(Iterable<Future<T>> futures, Executor executor) {
    return stream(futures, i -> CompletableFutures.allElementsAsync(i, executor),
        f -> CompletableFutures.newCompletableFuture(f, executor),
        cf -> FutureStreamBridge.ofAsync(cf, executor));
  }
}
