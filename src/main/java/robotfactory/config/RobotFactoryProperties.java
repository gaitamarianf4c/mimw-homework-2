package robotfactory.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import common.io.TextFileReader;

/**
 * Production settings loaded from robot_factory.properties.
 */
public final class RobotFactoryProperties
{
    public static final String KEY_FRAME_RATE = "frame_rate_per_sec";
    public static final String KEY_ENGINE_RATE = "engine_rate_per_sec";
    public static final String KEY_ASSEMBLY_TIME = "assembly_time_ms";
    public static final String KEY_ROBOT_COUNT = "robot_count";
    public static final String KEY_QUEUE_CAPACITY = "queue_capacity";

    private static final double MILLIS_PER_SECOND = 1000.0;

    private final double frameRatePerSec;
    private final double engineRatePerSec;
    private final long assemblyTimeMs;
    private final int robotCount;
    private final int queueCapacity;

    public RobotFactoryProperties(
        double frameRatePerSec,
        double engineRatePerSec,
        long assemblyTimeMs,
        int robotCount,
        int queueCapacity
    )
    {
        if (frameRatePerSec <= 0 || engineRatePerSec <= 0) {
            throw new IllegalArgumentException("rates have to be > 0");
        }
        if (assemblyTimeMs < 0) {
            throw new IllegalArgumentException(KEY_ASSEMBLY_TIME + " can't be negative");
        }
        if (robotCount <= 0 || queueCapacity <= 0) {
            throw new IllegalArgumentException(KEY_ROBOT_COUNT + " and " + KEY_QUEUE_CAPACITY + " have to be > 0");
        }

        this.frameRatePerSec = frameRatePerSec;
        this.engineRatePerSec = engineRatePerSec;
        this.assemblyTimeMs = assemblyTimeMs;
        this.robotCount = robotCount;
        this.queueCapacity = queueCapacity;
    }

    public static RobotFactoryProperties load(Path configPath) throws IOException
    {
        Properties properties = new TextFileReader(configPath).readProperties();

        try {
            return new RobotFactoryProperties(
                    Double.parseDouble(getRequiredValue(properties, KEY_FRAME_RATE)),
                    Double.parseDouble(getRequiredValue(properties, KEY_ENGINE_RATE)),
                    Long.parseLong(getRequiredValue(properties, KEY_ASSEMBLY_TIME)),
                    Integer.parseInt(getRequiredValue(properties, KEY_ROBOT_COUNT)),
                    Integer.parseInt(getRequiredValue(properties, KEY_QUEUE_CAPACITY)));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a number: " + e.getMessage(), e);
        }
    }

    public double getFrameRatePerSec()
    {
        return frameRatePerSec;
    }

    public double getEngineRatePerSec()
    {
        return engineRatePerSec;
    }

    public long getAssemblyTimeMs()
    {
        return assemblyTimeMs;
    }

    public int getRobotCount()
    {
        return robotCount;
    }

    public int getQueueCapacity()
    {
        return queueCapacity;
    }

    // rates are per second, Thread.sleep() wants the gap between two parts in ms
    public long getFrameIntervalMs()
    {
        return Math.round(MILLIS_PER_SECOND / frameRatePerSec);
    }

    public long getEngineIntervalMs()
    {
        return Math.round(MILLIS_PER_SECOND / engineRatePerSec);
    }

    private static String getRequiredValue(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("missing " + key);
        }
        return value.trim();
    }
}
