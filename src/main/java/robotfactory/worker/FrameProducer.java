package robotfactory.worker;

import common.console.ConsoleStyle;
import robotfactory.model.Frame;
import robotfactory.queue.BoundedBlockingQueue;

/**
 * Thread a): produces robot frames at the configured rate.
 */
public final class FrameProducer implements Runnable
{
    private final BoundedBlockingQueue<Frame> frameQueue;
    private final long productionIntervalMs;
    private final int framesToProduce;
    private final ConsoleStyle console;

    public FrameProducer(
        BoundedBlockingQueue<Frame> frameQueue,
        long productionIntervalMs,
        int framesToProduce,
        ConsoleStyle console
    )
    {
        this.frameQueue = frameQueue;
        this.productionIntervalMs = productionIntervalMs;
        this.framesToProduce = framesToProduce;
        this.console = console;
    }

    @Override
    public void run()
    {
        try {
            for (int serialNumber = 1; serialNumber <= framesToProduce; serialNumber++) {
                Thread.sleep(productionIntervalMs);
                Frame frame = new Frame(serialNumber);
                frameQueue.put(frame);
                console.text("  + " + frame + " produced");
            }
        } catch (InterruptedException e) {
            // restore the flag so whoever owns the thread can see it was interrupted
            Thread.currentThread().interrupt();
        }
    }
}
