package name.falgout.jeffrey.concurrent;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.Test;

import throwing.function.ThrowingSupplier;

public class CompletableFuturesTest extends BaseCompletableFuturesTest {
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
