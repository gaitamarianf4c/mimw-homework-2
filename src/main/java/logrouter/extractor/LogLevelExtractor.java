package logrouter.extractor;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes the level (2nd column) out of an Apache log line.
 */
public final class LogLevelExtractor
{
    // [Sun Dec 04 04:47:44 2005] [notice] ... -> group 1 = "notice"
    private static final Pattern LEVEL_PATTERN = Pattern.compile("^\\[[^\\]]*\\]\\s*\\[([^\\]]+)\\]");

    /**
     * @return the level, or empty if the line has none
     */
    public Optional<String> extractLevel(String line)
    {
        Matcher matcher = LEVEL_PATTERN.matcher(line);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(matcher.group(1).trim().toLowerCase(Locale.ROOT));
    }
}
