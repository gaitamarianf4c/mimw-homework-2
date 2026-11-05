package robotfactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import common.console.CommandOptions;
import common.console.ConsoleStyle;
import robotfactory.config.RobotFactoryProperties;
import robotfactory.model.Engine;
import robotfactory.model.Frame;
import robotfactory.queue.BoundedBlockingQueue;
import robotfactory.worker.EngineProducer;
import robotfactory.worker.FrameProducer;
import robotfactory.worker.RobotAssembler;

/**
 * Starts the three threads: a) frame producer, b) engine producer, c) robot assembler,
 * connected by two queues.
 */
public final class RobotFactoryApplication
{
    private static final String OPTION_CONFIG = "config";
    private static final String DEFAULT_CONFIG_PATH = "config/robot_factory.properties";

    private RobotFactoryApplication()
    {
    }

    public static void main(String[] args) throws InterruptedException
    {
        ConsoleStyle console = new ConsoleStyle();
        console.title("RobotFactory");

        RobotFactoryProperties properties;
        try {
            CommandOptions options = new CommandOptions(args, Set.of(OPTION_CONFIG));
            Path configPath = Path.of(options.getOption(OPTION_CONFIG, DEFAULT_CONFIG_PATH));
            properties = RobotFactoryProperties.load(configPath);
            console.text("Settings from <info>" + configPath + "</info>");
            console.text("");
        } catch (IllegalArgumentException e) {
            console.error(e.getMessage());
            System.exit(1);
            return;
        } catch (IOException e) {
            console.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(1);
            return;
        }
        
        printSettings(console, properties);

        int robotCount = properties.getRobotCount();
        BoundedBlockingQueue<Frame> frameQueue = new BoundedBlockingQueue<>("frame", properties.getQueueCapacity(), console);
        BoundedBlockingQueue<Engine> engineQueue = new BoundedBlockingQueue<>("engine", properties.getQueueCapacity(), console);

        Thread frameProducer = new Thread(
                new FrameProducer(frameQueue, properties.getFrameIntervalMs(), robotCount, console), "frame-producer");
        Thread engineProducer = new Thread(
                new EngineProducer(engineQueue, properties.getEngineIntervalMs(), robotCount, console), "engine-producer");
        Thread robotAssembler = new Thread(
                new RobotAssembler(frameQueue, engineQueue, properties.getAssemblyTimeMs(), robotCount, console),
                "robot-assembler");

        console.section("Production");
        frameProducer.start();
        engineProducer.start();
        robotAssembler.start();

        frameProducer.join();
        engineProducer.join();
        robotAssembler.join();

        console.success( robotCount + " robots assembled");
    }

    private static void printSettings(ConsoleStyle console, RobotFactoryProperties properties)
    {
        console.table(List.of("Setting", "Value"), List.of(
                List.of("Frames", properties.getFrameRatePerSec() + "/s (every " + properties.getFrameIntervalMs() + " ms)"),
                List.of("Engines", properties.getEngineRatePerSec() + "/s (every " + properties.getEngineIntervalMs() + " ms)"),
                List.of("Assembly time", properties.getAssemblyTimeMs() + " ms per robot"),
                List.of("Robots", String.valueOf(properties.getRobotCount())),
                List.of("Queue capacity", String.valueOf(properties.getQueueCapacity()))));
    }
}
