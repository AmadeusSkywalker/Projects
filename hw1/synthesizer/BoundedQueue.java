package synthesizer;

import java.util.Iterator;

/**
 * Created by vip on 2/16/17.
 */
interface BoundedQueue<T> extends Iterable<T> {
    int capacity();

    int fillCount();

    void enqueue(T x);

    T dequeue();

    T peek();

    default boolean isEmpty() {
        return fillCount() == 0;
    }

    default boolean isFull() {
        return fillCount() == capacity();
    }

    Iterator<T> iterator();
}
