package name.falgout.jeffrey.stream.future;

import throwing.stream.intermediate.ThrowingBaseStreamIntermediate;
import throwing.stream.terminal.ThrowingBaseStreamTerminal;

public interface FutureBaseStream<T, S extends FutureBaseStream<T, S>> extends
    ThrowingBaseStreamIntermediate<S>,
    ThrowingBaseStreamTerminal<T, Throwable, Throwable> {}
