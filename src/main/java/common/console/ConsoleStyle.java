package common.console;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Styled console output in the spirit of Symfony's SymfonyStyle: titles, tables and result blocks.
 *
 * Text may contain the tags &lt;info&gt;, &lt;comment&gt; and &lt;error&gt;; colors are dropped when NO_COLOR is set.
 */
public final class ConsoleStyle
{
    private static final int BLOCK_WIDTH = 80;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLACK_ON_GREEN = "\u001B[30;42m";
    private static final String BLACK_ON_YELLOW = "\u001B[30;43m";
    private static final String WHITE_ON_RED = "\u001B[97;41m";

    private static final Map<String, String> COLORS_BY_TAG = Map.of("info", GREEN, "comment", YELLOW, "error", RED);
    private static final Pattern TAG_PATTERN = Pattern.compile("</?(info|comment|error)>");

    private final PrintStream output;
    private final PrintStream errorOutput;
    private final boolean decorated;

    public ConsoleStyle()
    {
        this(System.out, System.err, System.getenv("NO_COLOR") == null && !"dumb".equals(System.getenv("TERM")));
    }

    public ConsoleStyle(
        PrintStream output,
        PrintStream errorOutput,
        boolean decorated
    )
    {
        this.output = output;
        this.errorOutput = errorOutput;
        this.decorated = decorated;
    }

    // every method is synchronized: the robot factory writes from three threads at once

    public synchronized void title(String message)
    {
        output.println();
        output.println(" " + colorize(YELLOW, message));
        output.println(" " + colorize(YELLOW, "=".repeat(message.length())));
        output.println();
    }

    public synchronized void section(String message)
    {
        output.println();
        output.println(" " + colorize(YELLOW, message));
        output.println(" " + colorize(YELLOW, "-".repeat(message.length())));
        output.println();
    }

    public synchronized void text(String message)
    {
        output.println(" " + format(message));
    }
    public synchronized void table(List<String> headers, List<List<String>> rows)
    {
        int[] columnWidths = new int[headers.size()];
        for (int column = 0; column < headers.size(); column++) {
            columnWidths[column] = headers.get(column).length();
            for (List<String> row : rows) {
                columnWidths[column] = Math.max(columnWidths[column], row.get(column).length());
            }
        }

        String border = buildBorder(columnWidths);
        output.println(border);
        output.println(colorize(GREEN, buildRow(headers, columnWidths)));
        output.println(border);
        for (List<String> row : rows) {
            output.println(buildRow(row, columnWidths));
        }
        output.println(border);
    }

    public synchronized void success(String message)
    {
        printBlock(output, "OK", message, BLACK_ON_GREEN);
    }

    public synchronized void warning(String message)
    {
        printBlock(output, "WARNING", message, BLACK_ON_YELLOW);
    }

    public synchronized void error(String message)
    {
        printBlock(errorOutput, "ERROR", message, WHITE_ON_RED);
    }

    private void printBlock(
        PrintStream stream,
        String type,
        String message,
        String colors
    )
    {
        String line = " [" + type + "] " + message;
        int width = Math.max(BLOCK_WIDTH, line.length() + 1);
        String paddedLine = line + " ".repeat(width - line.length());
        String emptyLine = " ".repeat(width);

        stream.println();
        stream.println(colorize(colors, emptyLine));
        stream.println(colorize(colors, paddedLine));
        stream.println(colorize(colors, emptyLine));
        stream.println();
    }

    private static String buildBorder(int[] columnWidths)
    {
        StringBuilder border = new StringBuilder();
        for (int columnWidth : columnWidths) {
            border.append(' ').append("-".repeat(columnWidth + 2));
        }
        return border.toString();
    }

    private static String buildRow(List<String> cells, int[] columnWidths)
    {
        StringBuilder row = new StringBuilder();
        for (int column = 0; column < cells.size(); column++) {
            String cell = cells.get(column);
            row.append("  ").append(cell).append(" ".repeat(columnWidths[column] - cell.length() + 1));
        }
        return row.toString();
    }

    private String format(String message)
    {
        Matcher matcher = TAG_PATTERN.matcher(message);
        if (!decorated) {
            return matcher.replaceAll("");
        }
        return matcher.replaceAll(match -> match.group().startsWith("</") ? RESET : COLORS_BY_TAG.get(match.group(1)));
    }

    private String colorize(String colors, String text)
    {
        return decorated ? colors + text + RESET : text;
    }
}
