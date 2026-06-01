package com.example.proxlogiq.controller;

import com.example.proxlogiq.dto.AnalyticsSummary;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.service.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummary> getAnalyticsSummary() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<Map<Integer, Long>> getStatusCodeDistribution() {
        return ResponseEntity.ok(analyticsService.getStatusCodeDistribution());
    }

    @GetMapping("/top-paths")
    public ResponseEntity<List<Map.Entry<String, Long>>> getTopPaths(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(analyticsService.getTopRequestedPaths(limit));
    }

    @GetMapping("/slowest")
    public ResponseEntity<List<LogEntry>> getSlowestRequests(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getSlowestRequests(limit));
    }

    @GetMapping("/invalid-reasons")
    public ResponseEntity<Map<String, Long>> getInvalidLogErrorReasonDistribution() {
        return ResponseEntity.ok(analyticsService.getInvalidLogErrorReasonDistribution());
    }
}
