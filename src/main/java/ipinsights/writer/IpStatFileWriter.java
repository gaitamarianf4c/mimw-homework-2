package ipinsights.writer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import common.io.TextFileWriter;
import ipinsights.model.IpOccurrence;

/**
 * Writes the ranking as one "ip count" pair per line.
 */
public final class IpStatFileWriter
{
    private static final String COLUMN_SEPARATOR = " ";

    private final Path statPath;

    public IpStatFileWriter(Path statPath)
    {
        this.statPath = statPath;
    }

    public void writeRanking(List<IpOccurrence> ranking) throws IOException
    {
        try (TextFileWriter writer = new TextFileWriter(statPath)) {
            for (IpOccurrence occurrence : ranking) {
                writer.writeLine(occurrence.getIpAddress() + COLUMN_SEPARATOR + occurrence.getCount());
            }
        }
    }
}
