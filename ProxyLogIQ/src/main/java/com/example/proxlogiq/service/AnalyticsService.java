package com.example.proxlogiq.service;

import com.example.proxlogiq.dto.AnalyticsSummary;
import com.example.proxlogiq.entity.InvalidLogEntry;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.repository.InvalidLogEntryRepository;
import com.example.proxlogiq.repository.LogEntryRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final LogEntryRepository logEntryRepository;
    private final InvalidLogEntryRepository invalidLogEntryRepository;

    public AnalyticsService(LogEntryRepository logEntryRepository,
                            InvalidLogEntryRepository invalidLogEntryRepository) {
        this.logEntryRepository = logEntryRepository;
        this.invalidLogEntryRepository = invalidLogEntryRepository;
    }

    public AnalyticsSummary getAnalyticsSummary() {
        long totalRequests = getTotalRequests();
        double cacheHitRatio = getCacheHitRatio();
        double averageLatency = getAverageLatency();
        long totalBytesTransferred = getTotalBytesTransferred();
        long invalidLogCount = getInvalidLogCount();
        Map<String, Long> percentiles = getLatencyPercentiles();

        return new AnalyticsSummary(
                totalRequests, cacheHitRatio, averageLatency,
                totalBytesTransferred, invalidLogCount,
                percentiles.get("p50"), percentiles.get("p95"), percentiles.get("p99")
        );
    }

    public long getTotalRequests() {
        return logEntryRepository.count();
    }

    public double getCacheHitRatio() {
        long total = getTotalRequests();
        if (total == 0) return 0.0;
        long hits = logEntryRepository.countByCacheHit(true);
        return (double) hits / total * 100.0;
    }

    public Map<Integer, Long> getStatusCodeDistribution() {
        List<LogEntry> allEntries = logEntryRepository.findAll();
        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()));
    }

    public List<LogEntry> getSlowestRequests(int limit) {
        return logEntryRepository.findByOrderByLatencyMsDesc(PageRequest.of(0, limit));
    }

    public List<Map.Entry<String, Long>> getTopRequestedPaths(int limit) {
        List<LogEntry> allEntries = logEntryRepository.findAll();
        return allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getPath, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double getAverageLatency() {
        List<LogEntry> allEntries = logEntryRepository.findAll();
        if (allEntries.isEmpty()) return 0.0;
        long sum = allEntries.stream().mapToLong(LogEntry::getLatencyMs).sum();
        return (double) sum / allEntries.size();
    }

    public Map<String, Long> getLatencyPercentiles() {
        List<LogEntry> allEntries = logEntryRepository.findAll();
        if (allEntries.isEmpty()) return Map.of("p50", 0L, "p95", 0L, "p99", 0L);

        List<Long> latencies = allEntries.stream()
                .mapToLong(LogEntry::getLatencyMs)
                .boxed().sorted().collect(Collectors.toList());

        int size = latencies.size();
        long p50 = latencies.get((int) Math.ceil(size * 0.5) - 1);
        long p95 = latencies.get((int) Math.ceil(size * 0.95) - 1);
        long p99 = latencies.get((int) Math.ceil(size * 0.99) - 1);
        return Map.of("p50", p50, "p95", p95, "p99", p99);
    }

    public long getTotalBytesTransferred() {
        List<LogEntry> allEntries = logEntryRepository.findAll();
        return allEntries.stream().mapToLong(LogEntry::getBytes).sum();
    }

    public long getInvalidLogCount() {
        return invalidLogEntryRepository.count();
    }

    public Map<String, Long> getInvalidLogErrorReasonDistribution() {
        List<InvalidLogEntry> allInvalidEntries = invalidLogEntryRepository.findAll();
        return allInvalidEntries.stream()
                .collect(Collectors.groupingBy(InvalidLogEntry::getErrorReason, Collectors.counting()));
    }
}
