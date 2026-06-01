package com.example.proxlogiq.service;

import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.repository.InvalidLogEntryRepository;
import com.example.proxlogiq.repository.LogEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private LogEntryRepository logEntryRepository;
    @Mock
    private InvalidLogEntryRepository invalidLogEntryRepository;

    private AnalyticsService service;

    private LogEntry entry1;
    private LogEntry entry2;
    private LogEntry entry3;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService(logEntryRepository, invalidLogEntryRepository);
        Instant now = Instant.now();
        entry1 = new LogEntry(now, "GET", "/api/users", 200, true, 10L, 100L);
        entry2 = new LogEntry(now.plusSeconds(1), "POST", "/api/orders", 201, false, 200L, 500L);
        entry3 = new LogEntry(now.plusSeconds(2), "GET", "/api/products", 500, true, 150L, 300L);
    }

    @Test
    void getTotalRequests_shouldReturnCount() {
        when(logEntryRepository.count()).thenReturn(3L);
        assertEquals(3, service.getTotalRequests());
    }

    @Test
    void getCacheHitRatio_shouldCalculateCorrectly() {
        when(logEntryRepository.count()).thenReturn(2L);
        when(logEntryRepository.countByCacheHit(true)).thenReturn(1L);
        assertEquals(50.0, service.getCacheHitRatio(), 0.001);
    }

    @Test
    void getCacheHitRatio_noEntries_shouldReturnZero() {
        when(logEntryRepository.count()).thenReturn(0L);
        assertEquals(0.0, service.getCacheHitRatio(), 0.001);
    }

    @Test
    void getAverageLatency_shouldCalculateCorrectly() {
        when(logEntryRepository.findAll()).thenReturn(List.of(entry1, entry2, entry3));
        double avg = (10.0 + 200.0 + 150.0) / 3.0;
        assertEquals(avg, service.getAverageLatency(), 0.001);
    }

    @Test
    void getStatusCodeDistribution_shouldGroupByStatusCode() {
        when(logEntryRepository.findAll()).thenReturn(List.of(entry1, entry2, entry3));
        Map<Integer, Long> distribution = service.getStatusCodeDistribution();
        assertEquals(1L, distribution.get(200));
        assertEquals(1L, distribution.get(201));
        assertEquals(1L, distribution.get(500));
    }

    @Test
    void getTopRequestedPaths_shouldReturnSorted() {
        when(logEntryRepository.findAll()).thenReturn(List.of(entry1, entry2, entry3));
        List<Map.Entry<String, Long>> topPaths = service.getTopRequestedPaths(2);
        assertEquals(2, topPaths.size());
    }

    @Test
    void getSlowestRequests_shouldCallRepository() {
        when(logEntryRepository.findByOrderByLatencyMsDesc(any(PageRequest.class)))
        .thenReturn(List.of(entry2, entry3, entry1));
        List<LogEntry> slowest = service.getSlowestRequests(5);
        assertEquals(3, slowest.size());
        assertEquals(200L, slowest.get(0).getLatencyMs());
    }

    @Test
    void getLatencyPercentiles_shouldCalculateCorrectly() {
        when(logEntryRepository.findAll()).thenReturn(List.of(entry1, entry2, entry3));
        Map<String, Long> percentiles = service.getLatencyPercentiles();
        assertTrue(percentiles.containsKey("p50"));
        assertTrue(percentiles.containsKey("p95"));
        assertTrue(percentiles.containsKey("p99"));
    }
}
