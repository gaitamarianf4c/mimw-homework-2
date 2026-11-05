package robotfactory.model;

/**
 * Robot engine
 */
public final class Engine
{
    private final int serialNumber;

    public Engine(int serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString()
    {
        return "Engine#" + serialNumber;
    }
}
