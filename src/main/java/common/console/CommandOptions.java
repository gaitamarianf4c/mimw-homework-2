package common.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Command line options written as --name=value or --name value.
 */
public final class CommandOptions
{
    private static final String OPTION_PREFIX = "--";

    private final Map<String, String> valuesByName = new HashMap<>();

    /**
     * @throws IllegalArgumentException for an unknown option or a missing value
     */
    public CommandOptions(String[] args, Set<String> optionNames)
    {
        for (int index = 0; index < args.length; index++) {
            String argument = args[index];
            if (!argument.startsWith(OPTION_PREFIX)) {
                throw new IllegalArgumentException("Unexpected argument \"" + argument + "\", options look like --name=value.");
            }

            String name;
            String value;
            int equalsIndex = argument.indexOf('=');
            if (equalsIndex >= 0) {
                name = argument.substring(OPTION_PREFIX.length(), equalsIndex);
                value = argument.substring(equalsIndex + 1);
            } else {
                name = argument.substring(OPTION_PREFIX.length());
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException("The \"--" + name + "\" option requires a value.");
                }
                index++;
                value = args[index];
            }

            if (!optionNames.contains(name)) {
                throw new IllegalArgumentException("The \"--" + name + "\" option does not exist.");
            }
            valuesByName.put(name, value);
        }
    }

    public String getOption(String name, String defaultValue)
    {
        return valuesByName.getOrDefault(name, defaultValue);
    }
}
