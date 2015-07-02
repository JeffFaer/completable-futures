package name.falgout.jeffrey.stream.future.adapter;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import throwing.stream.union.UnionThrowable;

class FutureThrowable extends UnionThrowable {
  private static final long serialVersionUID = -6300053386312753739L;

  FutureThrowable(Throwable cause) {
    super(cause);
  }

  public Error rethrow() throws CancellationException, InterruptedException, Throwable {
    return rethrow(ExecutionException.class, ExecutionException::getCause).rethrow(InterruptedException.class)
        .rethrow(CancellationException.class)
        .finish();
  }
}
