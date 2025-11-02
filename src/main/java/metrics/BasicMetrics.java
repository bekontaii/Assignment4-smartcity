package metrics;

import java.util.HashMap;
import java.util.Map;


public class BasicMetrics {
    private final Map<String, Long> counters = new HashMap<>();
    private long startTimeNs;
    private long endTimeNs;

    public void startTimer() {
        startTimeNs = System.nanoTime();
    }

    public void stopTimer() {
        endTimeNs = System.nanoTime();
    }

    public long getElapsedNs() {
        return endTimeNs - startTimeNs;
    }

    public void incCounter(String name) {
        counters.put(name, counters.getOrDefault(name, 0L) + 1);
    }

    public long getCounter(String name) {
        return counters.getOrDefault(name, 0L);
    }

    public Map<String, Long> snapshotAllCounters() {
        return Map.copyOf(counters);
    }
}
