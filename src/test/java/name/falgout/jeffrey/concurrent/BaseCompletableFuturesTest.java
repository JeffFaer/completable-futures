package name.falgout.jeffrey.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class BaseCompletableFuturesTest {
  @Rule public Timeout timeout = new Timeout(1000, TimeUnit.MILLISECONDS);

  public List<CompletableFuture<Integer>> futures;

  @Before
  public void setup() {
    futures = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      futures.add(new CompletableFuture<>());
    }
  }

  public void complete(int limit) {
    complete(0, limit);
  }

  public void complete(int start, int limit) {
    for (int i = start; i < limit; i++) {
      futures.get(i).complete(i);
    }
  }
}
