package ipinsights.model;

/**
 * How many times one IP address appears in the log.
 */
public final class IpOccurrence
{
    private final String ipAddress;
    private final int count;

    public IpOccurrence(String ipAddress, int count)
    {
        this.ipAddress = ipAddress;
        this.count = count;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public int getCount()
    {
        return count;
    }
}
