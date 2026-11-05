package robotfactory.model;

/**
 * Robot assembled from one frame and one engine.
 */
public final class Robot
{
    private final int serialNumber;
    private final Frame frame;
    private final Engine engine;

    public Robot(
        int serialNumber,
        Frame frame,
        Engine engine
    )
    {
        this.serialNumber = serialNumber;
        this.frame = frame;
        this.engine = engine;
    }

    @Override
    public String toString()
    {
        return "Robot#" + serialNumber + " (" + frame + " + " + engine + ")";
    }
}
