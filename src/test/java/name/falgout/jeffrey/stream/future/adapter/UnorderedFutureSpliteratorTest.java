package name.falgout.jeffrey.stream.future.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import name.falgout.jeffrey.concurrent.BaseCompletableFuturesTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnorderedFutureSpliteratorTest extends BaseCompletableFuturesTest {
  public UnorderedFutureSpliterator<Integer> spliterator;
  @Spy public LinkedBlockingQueue<Future<Integer>> queue;

  @Override
  @Before
  public void setup() {
    super.setup();

    spliterator = UnorderedFutureSpliterator.create(futures.spliterator(), queue);
  }

  @Test
  public void testPropogatesQueueingInterruptedException() throws InterruptedException {
    InterruptedException e = new InterruptedException();
    CountDownLatch l = new CountDownLatch(1);
    doAnswer(i -> {
      try {
        throw e;
      } finally {
        l.countDown();
      }
    }).when(queue).put(futures.get(0));

    complete(1);

    l.await();
    try {
      spliterator.tryAdvance(System.err::println);
      fail("expected exception");
    } catch (FutureThrowable ex) {
      assertSame(e, ex.getCause().getCause());
    }
  }

  @Test
  public void testPropogatesQueueingInterruptedExceptionIfAlreadyTaking()
      throws InterruptedException {
    InterruptedException e = new InterruptedException();
    doThrow(e).when(queue).put(futures.get(0));

    complete(1);

    try {
      spliterator.tryAdvance(System.err::println);
      fail("expected exception");
    } catch (FutureThrowable ex) {
      assertSame(e, ex.getCause().getCause());
    }
  }

  @Test
  public void testDoesNotBreakUnderFakeContention() throws InterruptedException, FutureThrowable {
    Iterator<? extends Future<Integer>> iterator = Arrays.<Future<Integer>> asList(null, null, null)
        .iterator();
    doAnswer(i -> {
      return iterator.hasNext() ? iterator.next() : i.callRealMethod();
    }).when(queue).poll();

    complete(1);

    assertTrue(spliterator.tryAdvance(i -> assertEquals(0, (int) i)));
  }
}
