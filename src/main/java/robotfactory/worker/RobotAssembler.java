package robotfactory.worker;

import common.console.ConsoleStyle;
import robotfactory.model.Engine;
import robotfactory.model.Frame;
import robotfactory.model.Robot;
import robotfactory.queue.BoundedBlockingQueue;

/**
 * Thread c): takes one frame and one engine from the queues and assembles a robot.
 */
public final class RobotAssembler implements Runnable
{
    private final BoundedBlockingQueue<Frame> frameQueue;
    private final BoundedBlockingQueue<Engine> engineQueue;
    private final long assemblyTimeMs;
    private final int robotsToAssemble;

    private final ConsoleStyle console;

    public RobotAssembler(
        BoundedBlockingQueue<Frame> frameQueue,
        BoundedBlockingQueue<Engine> engineQueue,
        long assemblyTimeMs,
        int robotsToAssemble,
        ConsoleStyle console
    )
    {
        this.frameQueue = frameQueue;
        this.engineQueue = engineQueue;
        this.assemblyTimeMs = assemblyTimeMs;
        this.robotsToAssemble = robotsToAssemble;
        this.console = console;
    }

    @Override
    public void run()
    {
        try {
            for (int serialNumber = 1; serialNumber <= robotsToAssemble; serialNumber++) {
                Frame frame = frameQueue.take();
                Engine engine = engineQueue.take();
                Thread.sleep(assemblyTimeMs);
                console.text("  <info>\u2714 New robot assembled:</info> " + new Robot(serialNumber, frame, engine));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
