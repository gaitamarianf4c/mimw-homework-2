package common.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes a UTF-8 text file line by line; missing parent directories are created.
 */
public final class TextFileWriter implements AutoCloseable
{
    private final Path path;
    private final BufferedWriter writer;
    private int lineCount;

    public TextFileWriter(Path path) throws IOException
    {
        this.path = path;

        Path directory = path.toAbsolutePath().getParent();
        if (directory != null) {
            Files.createDirectories(directory);
        }
        this.writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
    }

    public void writeLine(String line) throws IOException
    {
        writer.write(line);
        writer.newLine();
        lineCount++;
    }

    public Path getPath()
    {
        return path;
    }

    public int getLineCount()
    {
        return lineCount;
    }

    /**
     * Flushes the buffer; without it the end of the file can be lost.
     */
    @Override
    public void close() throws IOException
    {
        writer.close();
    }
}
