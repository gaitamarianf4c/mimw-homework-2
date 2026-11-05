package robotfactory.worker;

import common.console.ConsoleStyle;
import robotfactory.model.Engine;
import robotfactory.queue.BoundedBlockingQueue;

/**
 * Thread b): produces robot engines at the configured rate.
 */
public final class EngineProducer implements Runnable
{
    private final BoundedBlockingQueue<Engine> engineQueue;
    private final long productionIntervalMs;
    private final int enginesToProduce;
    private final ConsoleStyle console;

    public EngineProducer(
        BoundedBlockingQueue<Engine> engineQueue,
        long productionIntervalMs,
        int enginesToProduce,
        ConsoleStyle console
    )
    {
        this.engineQueue = engineQueue;
        this.productionIntervalMs = productionIntervalMs;
        this.enginesToProduce = enginesToProduce;
        this.console = console;
    }

    @Override
    public void run()
    {
        try {
            for (int serialNumber = 1; serialNumber <= enginesToProduce; serialNumber++) {
                Thread.sleep(productionIntervalMs);
                Engine engine = new Engine(serialNumber);
                engineQueue.put(engine);
                console.text("  + " + engine + " produced");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
