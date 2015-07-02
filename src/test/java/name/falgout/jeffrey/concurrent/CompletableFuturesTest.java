package name.falgout.jeffrey.concurrent;

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
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import name.falgout.jeffrey.stream.future.FutureStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CompletableFuturesTest {
  private List<CompletableFuture<Integer>> futures = new ArrayList<>();

  @Before
  public void setup() {
    for (int i = 0; i < 100; i++) {
      futures.add(new CompletableFuture<>());
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
  public void testNewThrowsCorrectExecutionException() throws InterruptedException,
      ExecutionException {
    IOException cause = new IOException();
    ExecutionException e = new ExecutionException(cause);

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    CompletableFuture<Void> cf = CompletableFutures.newCompletableFuture(f);
    try {
      cf.get();
      fail("expected exception");
    } catch (ExecutionException ex) {
      assertSame(cause, ex.getCause());
    }
  }

  @Test
  public void testNewThrowsCorrectCancellationException() throws InterruptedException,
      ExecutionException {
    CancellationException e = new CancellationException();

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    CompletableFuture<Void> cf = CompletableFutures.newCompletableFuture(f);
    try {
      cf.get();
      fail("expected exception");
    } catch (CancellationException ex) {
      assertSame(e, ex);
    }
  }

  @Test
  public void testNewThrowsCorrectInterruptedException() throws InterruptedException,
      ExecutionException {
    InterruptedException e = new InterruptedException();

    Future<Void> f = mock(Future.class);
    when(f.get()).thenThrow(e);

    CompletableFuture<Void> cf = CompletableFutures.newCompletableFuture(f);
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
  public void testfutureStreamThrowsCorrectCancellationException() throws InterruptedException,
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
  public void testFutureStream() throws InterruptedException, ExecutionException {
    FutureStream<Integer> stream = CompletableFutures.stream(futures);
    Future<Integer> sum = stream.mapToInt(i -> i).sum();
    assertFalse(sum.isDone());

    for (int i = 0; i < 99; i++) {
      futures.get(i).complete(i);
    }

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

    for (int i = 0; i < 99; i++) {
      futures.get(i).complete(i);
    }

    l.await();
    assertFalse(collected.isDone());
    for (int i = 0; i < 98; i++) {
      Mockito.verify(mockAccumulator).accept(0, i);
    }

    futures.get(99).complete(99);

    collected.get();
    verify(mockAccumulator).accept(0, 99);
  }

}
