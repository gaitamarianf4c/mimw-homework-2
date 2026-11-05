package robotfactory.model;

/**
 * Robot frame
 */
public final class Frame
{
    private final int serialNumber;

    public Frame(int serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString()
    {
        return "Frame#" + serialNumber;
    }
}
