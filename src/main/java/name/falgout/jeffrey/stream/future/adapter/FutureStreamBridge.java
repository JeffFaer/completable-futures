package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;
import throwing.stream.union.UnionStream;

public final class FutureStreamBridge {
  private FutureStreamBridge() {}

  public static <T> FutureStream<T> of(Stream<? extends Future<T>> futureStream) {
    return new FutureStreamAdapter<>(unionize(futureStream));
  }

  public static <T> FutureStream<T> of(Stream<? extends Future<T>> futureStream, Executor executor) {
    return new FutureStreamAdapter<>(unionize(futureStream), executor);
  }

  private static <T> UnionStream<T, FutureThrowable> unionize(
      Stream<? extends Future<T>> futureStream) {
    return UnionStream.of(futureStream, FutureThrowable::new).map(Future::get);
  }

  public static FutureIntStream ofInt(Stream<Future<? extends Integer>> futureStream) {
    return new FutureIntStreamAdapter(unionizeInt(futureStream));
  }

  public static FutureIntStream ofInt(Stream<Future<? extends Integer>> futureStream,
      Executor executor) {
    return new FutureIntStreamAdapter(unionizeInt(futureStream), executor);
  }

  private static UnionIntStream<FutureThrowable> unionizeInt(
      Stream<Future<? extends Integer>> futureStream) {
    return UnionStream.of(futureStream, FutureThrowable::new).mapToInt(Future::get);
  }

  public static FutureLongStream ofLong(Stream<Future<? extends Long>> futureStream) {
    return new FutureLongStreamAdapter(unionizeLong(futureStream));
  }

  public static FutureLongStream ofLong(Stream<Future<? extends Long>> futureStream,
      Executor executor) {
    return new FutureLongStreamAdapter(unionizeLong(futureStream), executor);
  }

  private static UnionLongStream<FutureThrowable> unionizeLong(
      Stream<Future<? extends Long>> futureStream) {
    return UnionStream.of(futureStream, FutureThrowable::new).mapToLong(Future::get);
  }

  public static FutureDoubleStream ofDouble(Stream<Future<? extends Double>> futureStream) {
    return new FutureDoubleStreamAdapter(unionizeDouble(futureStream));
  }

  public static FutureDoubleStream ofDouble(Stream<Future<? extends Double>> futureStream,
      Executor executor) {
    return new FutureDoubleStreamAdapter(unionizeDouble(futureStream), executor);
  }

  private static UnionDoubleStream<FutureThrowable> unionizeDouble(
      Stream<Future<? extends Double>> futureStream) {
    return UnionStream.of(futureStream, FutureThrowable::new).mapToDouble(Future::get);
  }
}
