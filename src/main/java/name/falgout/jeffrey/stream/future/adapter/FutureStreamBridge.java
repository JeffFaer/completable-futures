package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;

public final class FutureStreamBridge {
  private FutureStreamBridge() {}

  public static <T> FutureStream<T> of(CompletableFuture<Stream<T>> futureStream) {
    return new FutureStreamAdapter<>(futureStream);
  }

  public static <T> FutureStream<T> ofAsync(CompletableFuture<Stream<T>> futureStream) {
    return new FutureStreamAdapter<>(futureStream, (Void) null);
  }

  public static <T> FutureStream<T> ofAsync(CompletableFuture<Stream<T>> futureStream,
      Executor executor) {
    return new FutureStreamAdapter<>(futureStream, executor);
  }

  public static FutureIntStream ofInt(CompletableFuture<IntStream> futureStream) {
    return new FutureIntStreamAdapter(futureStream);
  }

  public static FutureIntStream ofIntAsync(CompletableFuture<IntStream> futureStream) {
    return new FutureIntStreamAdapter(futureStream, (Void) null);
  }

  public static FutureIntStream ofIntAsync(CompletableFuture<IntStream> futureStream,
      Executor executor) {
    return new FutureIntStreamAdapter(futureStream, executor);
  }

  public static FutureLongStream ofLong(CompletableFuture<LongStream> futureStream) {
    return new FutureLongStreamAdapter(futureStream);
  }

  public static FutureLongStream ofLongAsync(CompletableFuture<LongStream> futureStream) {
    return new FutureLongStreamAdapter(futureStream, (Void) null);
  }

  public static FutureLongStream ofLongAsync(CompletableFuture<LongStream> futureStream,
      Executor executor) {
    return new FutureLongStreamAdapter(futureStream, executor);
  }

  public static FutureDoubleStream ofDouble(CompletableFuture<DoubleStream> futureStream) {
    return new FutureDoubleStreamAdapter(futureStream);
  }

  public static FutureDoubleStream ofDoubleAsync(CompletableFuture<DoubleStream> futureStream) {
    return new FutureDoubleStreamAdapter(futureStream, (Void) null);
  }

  public static FutureDoubleStream ofDoubleAsync(CompletableFuture<DoubleStream> futureStream,
      Executor executor) {
    return new FutureDoubleStreamAdapter(futureStream, executor);
  }

  public static <T, A, R> Collector<CompletableFuture<T>, ?, CompletableFuture<R>> collector(
      Collector<T, A, R> collector) {
    return new CompletableFutureCollector<>(collector);
  }

  public static <T, A, R> Collector<CompletableFuture<T>, ?, CompletableFuture<R>> collectorAsync(
      Collector<T, A, R> collector) {
    return new CompletableFutureCollector<>(collector, (Void) null);
  }

  public static <T, A, R> Collector<CompletableFuture<T>, ?, CompletableFuture<R>> collectorAsync(
      Collector<T, A, R> collector, Executor executor) {
    return new CompletableFutureCollector<>(collector, executor);
  }
}
