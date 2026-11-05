package ipinsights.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ipinsights.model.IpOccurrence;

/**
 * Counts occurrences per IP address.
 */
public final class IpOccurrenceCounter
{
    private final Map<String, Integer> countsByIpAddress = new LinkedHashMap<>();

    public void recordOccurrence(String ipAddress)
    {
        countsByIpAddress.merge(ipAddress, 1, Integer::sum);
    }

    /**
     * @return occurrences, most frequent first
     */
    public List<IpOccurrence> getRanking()
    {
        List<IpOccurrence> ranking = new ArrayList<>();
        countsByIpAddress.forEach((ipAddress, count) -> ranking.add(new IpOccurrence(ipAddress, count)));
        ranking.sort(IpOccurrenceCounter::compareByCountThenIpAddress);
        return ranking;
    }

    // ties sorted by IP so the output is the same on every run
    private static int compareByCountThenIpAddress(IpOccurrence first, IpOccurrence second)
    {
        int countComparison = Integer.compare(second.getCount(), first.getCount());
        if (countComparison != 0) {
            return countComparison;
        }
        return first.getIpAddress().compareTo(second.getIpAddress());
    }
}
