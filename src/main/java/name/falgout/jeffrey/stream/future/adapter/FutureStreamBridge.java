package name.falgout.jeffrey.stream.future.adapter;

import java.util.Spliterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.StreamSupport;

import name.falgout.jeffrey.stream.future.FutureDoubleStream;
import name.falgout.jeffrey.stream.future.FutureIntStream;
import name.falgout.jeffrey.stream.future.FutureLongStream;
import name.falgout.jeffrey.stream.future.FutureStream;
import throwing.ThrowingBaseSpliterator.ThrowingSpliterator;
import throwing.stream.adapter.ThrowingBridge;
import throwing.stream.union.UnionDoubleStream;
import throwing.stream.union.UnionIntStream;
import throwing.stream.union.UnionLongStream;
import throwing.stream.union.UnionStream;
import throwing.stream.union.adapter.UnionBridge;

public final class FutureStreamBridge {
  private FutureStreamBridge() {}

  public static <T> FutureStream<T> of(Spliterator<? extends Future<T>> futures) {
    return new FutureStreamAdapter<>(unionize(futures));
  }

  public static <T> FutureStream<T> of(Spliterator<? extends Future<T>> futures, Executor executor) {
    return new FutureStreamAdapter<>(unionize(futures), executor);
  }

  private static <T> UnionStream<T, FutureThrowable> unionize(
      Spliterator<? extends Future<T>> futures) {
    if (!futures.hasCharacteristics(Spliterator.ORDERED)) {
      ThrowingSpliterator<T, FutureThrowable> unorderedFutures = UnorderedFutureSpliterator.create(futures);
      return UnionBridge.of(ThrowingBridge.stream(unorderedFutures, FutureThrowable.class),
          FutureThrowable.class, FutureThrowable::new);
    } else {
      return UnionStream.of(StreamSupport.stream(futures, false), FutureThrowable::new).map(
          Future::get);
    }
  }

  public static FutureIntStream ofInt(Spliterator<? extends Future<Integer>> futures) {
    return new FutureIntStreamAdapter(unionizeInt(futures));
  }

  public static FutureIntStream ofInt(Spliterator<? extends Future<Integer>> futures,
      Executor executor) {
    return new FutureIntStreamAdapter(unionizeInt(futures), executor);
  }

  private static UnionIntStream<FutureThrowable> unionizeInt(
      Spliterator<? extends Future<Integer>> futures) {
    return unionize(futures).mapToInt(i -> i);
  }

  public static FutureLongStream ofLong(Spliterator<? extends Future<Long>> futures) {
    return new FutureLongStreamAdapter(unionizeLong(futures));
  }

  public static FutureLongStream ofLong(Spliterator<? extends Future<Long>> futures,
      Executor executor) {
    return new FutureLongStreamAdapter(unionizeLong(futures), executor);
  }

  private static UnionLongStream<FutureThrowable> unionizeLong(
      Spliterator<? extends Future<Long>> futures) {
    return unionize(futures).mapToLong(l -> l);
  }

  public static FutureDoubleStream ofDouble(Spliterator<? extends Future<Double>> futures) {
    return new FutureDoubleStreamAdapter(unionizeDouble(futures));
  }

  public static FutureDoubleStream ofDouble(Spliterator<? extends Future<Double>> futures,
      Executor executor) {
    return new FutureDoubleStreamAdapter(unionizeDouble(futures), executor);
  }

  private static UnionDoubleStream<FutureThrowable> unionizeDouble(
      Spliterator<? extends Future<Double>> futures) {
    return unionize(futures).mapToDouble(d -> d);
  }
}
