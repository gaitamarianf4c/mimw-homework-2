package logrouter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import common.console.CommandOptions;
import common.console.ConsoleStyle;
import common.io.TextFileReader;
import logrouter.extractor.LogLevelExtractor;
import logrouter.writer.LogWriter;

/**
 * Reads the Apache log line by line and writes each line into the file of its level.
 *
 * Usage: ./start.sh log-router [--log=PATH]
 */
public final class LogRouterApplication
{
    private static final String OPTION_LOG = "log";
    private static final String DEFAULT_LOG_PATH = "var/logs/apache_2k.log";

    private LogRouterApplication()
    {
    }

    public static void main(String[] args)
    {
        ConsoleStyle console = new ConsoleStyle();
        console.title("LogRouter");

        try {
            CommandOptions options = new CommandOptions(args, Set.of(OPTION_LOG));
            Path logPath = Path.of(options.getOption(OPTION_LOG, DEFAULT_LOG_PATH));
            console.text("Routing <info>" + logPath + "</info> by level");
            console.text("");

            LogLevelExtractor levelExtractor = new LogLevelExtractor();
            List<List<String>> rows = new ArrayList<>();
            int lineCount;

            try (LogWriter logWriter = new LogWriter(logPath)) {
                lineCount = new TextFileReader(logPath).forEachLine(line ->
                        levelExtractor.extractLevel(line)
                                .ifPresent(level -> logWriter.write(level, line)));

                logWriter.getLineCountsByLevel().forEach((level, levelLineCount) -> {
                    String levelFileName = logWriter.getLevelFileName(level);
                    console.text("Writing into <info>" + logPath.resolveSibling(levelFileName) + "</info>");
                    rows.add(List.of(levelFileName, String.valueOf(levelLineCount)));
                });
            }

            console.text("");
            console.table(List.of("File", "Lines"), rows);
            console.success(lineCount + " lines routed into " + rows.size() + " files");
        } catch (IllegalArgumentException e) {
            console.error(e.getMessage());
            System.exit(1);
        } catch (IOException | UncheckedIOException e) {
            console.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
