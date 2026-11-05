package common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Reads a UTF-8 text file.
 */
public final class TextFileReader
{
    private final Path path;

    public TextFileReader(Path path)
    {
        this.path = path;
    }

    /**
     * Goes through the file one line at a time, so a large file is never fully loaded in memory.
     *
     * @return number of lines read
     */
    public int forEachLine(Consumer<String> lineHandler) throws IOException
    {
        int lineCount = 0;
        try (BufferedReader reader = openReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineHandler.accept(line);
                lineCount++;
            }
        }
        return lineCount;
    }

    public Properties readProperties() throws IOException
    {
        Properties properties = new Properties();
        try (BufferedReader reader = openReader()) {
            properties.load(reader);
        }
        return properties;
    }

    private BufferedReader openReader() throws IOException
    {
        return Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }
}
