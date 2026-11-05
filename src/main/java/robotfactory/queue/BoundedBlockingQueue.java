package robotfactory.queue;

import java.util.ArrayDeque;
import java.util.Deque;

import common.console.ConsoleStyle;

/**
 * Producer-consumer queue with a fixed capacity: put() waits while it is full, take() while it is empty.
 */
public final class BoundedBlockingQueue<T>
{
    private final Deque<T> items = new ArrayDeque<>();
    private final String name;
    private final int capacity;
    private final ConsoleStyle console;

    public BoundedBlockingQueue(
        String name,
        int capacity,
        ConsoleStyle console
    )
    {
        this.name = name;
        this.capacity = capacity;
        this.console = console;
    }


    public synchronized void put(T item) throws InterruptedException
    {
        // while, not if: another producer may fill the queue again before this one wakes up
        while (items.size() == capacity) {
            console.text("  <comment>! " + name + " queue is full, producer waits</comment>");
            wait();
        }
        items.addLast(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException
    {
        while (items.isEmpty()){
            wait();
        }
        T item = items.removeFirst();
        notifyAll();
        return item;
    }
}
