package ipinsights;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import common.console.CommandOptions;
import common.console.ConsoleStyle;
import common.io.TextFileReader;
import ipinsights.extractor.Ipv4AddressExtractor;
import ipinsights.model.IpOccurrence;
import ipinsights.service.IpOccurrenceCounter;
import ipinsights.writer.IpStatFileWriter;

/**
 * Extracts the IPv4 addresses from the Linux log and writes each one with its number of occurrences.
 *
 * Usage: ./start.sh ip-insights [--log=PATH] [--stat=PATH]
 */
public final class IpInsightsApplication
{
    private static final String OPTION_LOG = "log";
    private static final String OPTION_STAT = "stat";
    private static final String DEFAULT_LOG_PATH = "var/logs/linux_2k.log";
    private static final String DEFAULT_STAT_PATH = "var/logs/linux_2k_ip_stat.txt";
    private static final int TOP_ADDRESS_COUNT = 10;

    private IpInsightsApplication()
    {
    }

    public static void main(String[] args)
    {
        ConsoleStyle console = new ConsoleStyle();
        console.title("IpInsights");

        try {
            CommandOptions options = new CommandOptions(args, Set.of(OPTION_LOG, OPTION_STAT));
            Path logPath = Path.of(options.getOption(OPTION_LOG, DEFAULT_LOG_PATH));
            Path statPath = Path.of(options.getOption(OPTION_STAT, DEFAULT_STAT_PATH));
            console.text("Extracting IPv4 addresses from <info>" + logPath + "</info>");

            Ipv4AddressExtractor addressExtractor = new Ipv4AddressExtractor();
            IpOccurrenceCounter occurrenceCounter = new IpOccurrenceCounter();
            int lineCount = new TextFileReader(logPath).forEachLine(line ->
                    addressExtractor.extractAddresses(line).forEach(occurrenceCounter::recordOccurrence));

            List<IpOccurrence> ranking = occurrenceCounter.getRanking();
            console.text("Writing into <info>" + statPath + "</info>");
            new IpStatFileWriter(statPath).writeRanking(ranking);

            console.section("Top " + TOP_ADDRESS_COUNT + " addresses");
            List<List<String>> rows = new ArrayList<>();
            for (IpOccurrence occurrence : ranking.subList(0, Math.min(TOP_ADDRESS_COUNT, ranking.size()))) {
                rows.add(List.of(occurrence.getIpAddress(), String.valueOf(occurrence.getCount())));
            }
            console.table(List.of("IP address", "Occurrences"), rows);

            console.success(lineCount + " lines read, " + ranking.size() + " distinct IPs");
        } catch (IllegalArgumentException e) {
            console.error(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            console.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
