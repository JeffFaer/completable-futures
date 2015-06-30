package name.falgout.jeffrey.stream.future;

import java.util.stream.BaseStream;

public interface FutureBaseStream<T, S extends FutureBaseStream<T, S>> extends BaseStream<T, S> {
    @Override
    public FutureSpliterator<T> spliterator();

    @Override
    public FutureIterator<T> iterator();
}
