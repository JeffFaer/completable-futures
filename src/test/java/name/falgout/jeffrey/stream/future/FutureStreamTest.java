package name.falgout.jeffrey.stream.future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;

import name.falgout.jeffrey.concurrent.BaseCompletableFuturesTest;
import name.falgout.jeffrey.concurrent.CompletableFutures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FutureStreamTest extends BaseCompletableFuturesTest {
  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[] { "unordered", (Supplier<?>) HashSet::new, 10, 50 },
        new Object[] { "ordered", (Supplier<?>) ArrayList::new, 0, 99 });
  }

  public Supplier<Collection<Future<Integer>>> factory;
  // indices for partial calculation
  public int start;
  public int limit;

  public Collection<Future<Integer>> wrappedFutures;

  public FutureStreamTest(String testName, Supplier<Collection<Future<Integer>>> factory,
      int start, int limit) {
    this.factory = factory;
    this.start = start;
    this.limit = limit;
  }

  @Override
  @Before
  public void setup() {
    super.setup();

    wrappedFutures = factory.get();
    wrappedFutures.addAll(super.futures);
  }

  @Test
  public void testFutureStreamThrowsCorrectExecutionException() throws InterruptedException,
      ExecutionException {
    IOException cause = new IOException();
    ExecutionException e = new ExecutionException(cause);

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    Future<?> cf = CompletableFutures.stream(f).count();
    try {
      cf.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(cause, ex.getCause());
    }
  }

  @Test
  public void testFutureStreamThrowsCorrectCancellationException() throws InterruptedException,
      ExecutionException {
    CancellationException e = new CancellationException();

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    Future<?> cf = CompletableFutures.stream(f).count();
    try {
      cf.get();
      fail("expected exception");
    } catch (CancellationException ex) {
      assertSame(e, ex);
    }
  }

  @Test
  public void testFutureStreamThrowsCorrectInterruptedException() throws InterruptedException,
      ExecutionException {
    InterruptedException e = new InterruptedException();

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    Future<?> cf = CompletableFutures.stream(f).count();
    try {
      cf.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(e, ex.getCause());
    }
  }

  @Test
  public void testFutureStreamThrowsCorrectOperationException() throws InterruptedException {
    RuntimeException e = new RuntimeException();
    FutureStream<Integer> stream = CompletableFutures.stream(wrappedFutures).filter(i -> {
      throw e;
    });

    complete(100);
    Future<Long> count = stream.count();

    try {
      count.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(e, ex.getCause());
    }
  }

  @Test
  public void testFutureStream() throws InterruptedException, ExecutionException {
    FutureStream<Integer> stream = CompletableFutures.stream(wrappedFutures);
    Future<Integer> sum = stream.mapToInt(i -> i).sum();

    complete(99);
    assertFalse(sum.isDone());
    super.futures.get(99).complete(99);

    assertEquals(99 * 100 / 2, (int) sum.get());
  }

  @Test
  public void testPartialCalculation() throws InterruptedException, ExecutionException {
    FutureStream<Integer> stream = CompletableFutures.stream(wrappedFutures);

    BiConsumer<Integer, Integer> mockAccumulator = mock(BiConsumer.class);
    CountDownLatch l = new CountDownLatch(limit - start - 1);
    doAnswer(i -> {
      l.countDown();
      return null;
    }).when(mockAccumulator).accept(anyInt(), anyInt());
    Collector<Integer, Integer, Integer> personalSum = Collector.of(() -> 0, mockAccumulator, (i1,
        i2) -> i1 + i2);

    Future<Integer> collected = stream.collect(personalSum);
    assertFalse(collected.isDone());
    complete(start, limit);

    l.await();
    assertFalse(collected.isDone());
    for (int i = start; i < limit; i++) {
      verify(mockAccumulator).accept(0, i);
    }

    complete(0, start);
    complete(limit, futures.size());

    collected.get();
    for (int i = 0; i < start; i++) {
      verify(mockAccumulator).accept(0, i);
    }
    for (int i = limit; i < futures.size(); i++) {
      verify(mockAccumulator).accept(0, i);
    }
  }
}
