package name.falgout.jeffrey.concurrent;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import name.falgout.jeffrey.stream.future.FutureStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import throwing.function.ThrowingSupplier;

public class CompletableFuturesTest {
  @Rule public Timeout timeout = new Timeout(500, TimeUnit.MILLISECONDS);

  private List<CompletableFuture<Integer>> futures;

  @Before
  public void setup() {
    futures = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      futures.add(new CompletableFuture<>());
    }
  }

  public void complete(int limit) {
    for (int i = 0; i < limit; i++) {
      futures.get(i).complete(i);
    }
  }

  @Test
  public void testFailedThrowsCorrectException() throws InterruptedException {
    IOException e = new IOException();
    CompletableFuture<Void> f = CompletableFutures.failed(e);

    try {
      f.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(e, ex.getCause());
    }
  }

  @Test
  public void testNewThrowsCorrectCancellationException() throws Throwable {
    CancellationException e = new CancellationException();

    ThrowingSupplier<Void, ?> s = mock(ThrowingSupplier.class);
    when(s.get()).thenThrow(e);

    CompletableFuture<Void> cf = CompletableFutures.newCompletableFuture(s);
    try {
      cf.get();
      fail("expected exception");
    } catch (CancellationException ex) {
      assertSame(e, ex);
    }
  }

  @Test
  public void testNewThrowsCorrectInterruptedException() throws Throwable {
    InterruptedException e = new InterruptedException();

    ThrowingSupplier<Void, ?> s = mock(ThrowingSupplier.class);
    when(s.get()).thenThrow(e);

    CompletableFuture<Void> cf = CompletableFutures.newCompletableFuture(s);
    try {
      cf.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(e, ex.getCause());
    }
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
    FutureStream<Integer> stream = CompletableFutures.stream(futures).filter(i -> {
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
    FutureStream<Integer> stream = CompletableFutures.stream(futures);
    Future<Integer> sum = stream.mapToInt(i -> i).sum();

    complete(99);
    assertFalse(sum.isDone());
    futures.get(99).complete(99);

    assertEquals(99 * 100 / 2, (int) sum.get());
  }

  @Test
  public void testPartialCalculation() throws InterruptedException, ExecutionException {
    FutureStream<Integer> stream = CompletableFutures.stream(futures);

    BiConsumer<Integer, Integer> mockAccumulator = mock(BiConsumer.class);
    CountDownLatch l = new CountDownLatch(98);
    doAnswer(i -> {
      l.countDown();
      return null;
    }).when(mockAccumulator).accept(anyInt(), anyInt());
    Collector<Integer, Integer, Integer> personalSum = Collector.of(() -> 0, mockAccumulator, (i1,
        i2) -> i1 + i2);

    Future<Integer> collected = stream.collect(personalSum);
    assertFalse(collected.isDone());
    complete(99);

    l.await();
    assertFalse(collected.isDone());
    for (int i = 0; i < 98; i++) {
      verify(mockAccumulator).accept(0, i);
    }

    futures.get(99).complete(99);

    collected.get();
    verify(mockAccumulator).accept(0, 99);
  }

  @Test
  public void testAnyOfIsNotSequential() throws InterruptedException, ExecutionException {
    Future<Optional<Integer>> i = CompletableFutures.anyOf(futures);

    futures.get(16).complete(16);

    assertTrue(i.get().isPresent());
    assertEquals(16, (int) i.get().get());
  }

  @Test
  public void testNullAnyOf() throws InterruptedException, ExecutionException {
    Future<Optional<Integer>> i = CompletableFutures.anyOf();
    assertTrue(i.isDone());
    assertFalse(i.get().isPresent());
  }

  @Test
  public void testAllOf() throws InterruptedException, ExecutionException {
    Future<List<Integer>> i = CompletableFutures.allOf(futures);
    complete(100);

    assertEquals(IntStream.range(0, 100).boxed().collect(toList()), i.get());
  }

  @Test
  public void testNullAllOf() throws InterruptedException, ExecutionException {
    Future<List<Integer>> i = CompletableFutures.allOf();
    assertTrue(i.isDone());
    assertTrue(i.get().isEmpty());
  }

  @Test
  public void testFutureGetThrowsUnwrappedException() throws InterruptedException,
      ExecutionException {
    IOException cause = new IOException();
    Future<Void> f = CompletableFutures.failed(cause);

    ThrowingSupplier<?, ?> s = CompletableFutures.get(f);
    try {
      s.get();
      fail("expected exception");
    } catch (Throwable ex) {
      assertSame(cause, ex);
    }
  }
}
