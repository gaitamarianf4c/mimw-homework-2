package logrouter.writer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import common.io.TextFileWriter;

/**
 * Writes each log line into the file of its level, next to the source log.
 */
public final class LogWriter implements AutoCloseable
{
    /** apache_2k.log + notice -> apache_2k_notice.log (the source extension is kept) */
    private static final String FILE_NAME_FORMAT = "{filename}_{level}";
    private static final String FILE_NAME_PLACEHOLDER = "{filename}";
    private static final String LEVEL_PLACEHOLDER = "{level}";

    private final Path directory;
    private final String baseName;
    private final String extension;
    private final Map<String, TextFileWriter> writersByLevel = new LinkedHashMap<>();

    public LogWriter(Path logPath)
    {
        this.directory = logPath.toAbsolutePath().getParent();

        String logFileName = logPath.getFileName().toString().toLowerCase(Locale.ROOT);
        int extensionStart = logFileName.lastIndexOf('.');
        this.baseName = extensionStart > 0 ? logFileName.substring(0, extensionStart) : logFileName;
        this.extension = extensionStart > 0 ? logFileName.substring(extensionStart) : "";
    }

    public void write(String level, String line)
    {
        try {
            // opened on first use, so levels we didn't know about still get a file
            TextFileWriter writer = writersByLevel.get(level);
            if (writer == null) {
                writer = new TextFileWriter(directory.resolve(getLevelFileName(level)));
                writersByLevel.put(level, writer);
            }
            writer.writeLine(line);
        } catch (IOException e) {
            // unchecked because write() is called from a lambda
            throw new UncheckedIOException("Cannot write " + getLevelFileName(level), e);
        }
    }

    public String getLevelFileName(String level)
    {
        return FILE_NAME_FORMAT
                .replace(FILE_NAME_PLACEHOLDER, baseName)
                .replace(LEVEL_PLACEHOLDER, toSnakeCase(level))
                + extension;
    }

    public Map<String, Integer> getLineCountsByLevel()
    {
        Map<String, Integer> lineCountsByLevel = new LinkedHashMap<>();
        writersByLevel.forEach((level, writer) -> lineCountsByLevel.put(level, writer.getLineCount()));
        return lineCountsByLevel;
    }

    @Override
    public void close() throws IOException
    {
        // every file is closed even if one of them fails
        IOException firstError = null;
        for (TextFileWriter writer : writersByLevel.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                if (firstError == null) {
                    firstError = e;
                }
            }
        }
        if (firstError != null) {
            throw firstError;
        }
    }

    private static String toSnakeCase(String level)
    {
        return level.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
    }
}
