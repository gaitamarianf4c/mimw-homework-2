package ipinsights.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds the IPv4 addresses in a log line.
 */
public final class Ipv4AddressExtractor
{
    // 0-255, so 999.1.1.1 is rejected
    private static final String OCTET = "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)";

    // Ignores digits glued to other text: the log has host names like
    // host129.206.196.21.maximumasp.com, which are not addresses.
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "(?<![\\w.-])" + OCTET + "\\." + OCTET + "\\." + OCTET + "\\." + OCTET + "(?![\\w-]|\\.\\w)");

    public List<String> extractAddresses(String line)
    {
        List<String> ipAddresses = new ArrayList<>();
        Matcher matcher = ADDRESS_PATTERN.matcher(line);
        while (matcher.find()) {
            ipAddresses.add(matcher.group());
        }
        return ipAddresses;
    }
}
