package com.example.proxlogiq.controller;

import com.example.proxlogiq.entity.InvalidLogEntry;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.repository.InvalidLogEntryRepository;
import com.example.proxlogiq.service.LogFilterService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogQueryController {

    private final LogFilterService logFilterService;
    private final InvalidLogEntryRepository invalidLogEntryRepository;

    public LogQueryController(LogFilterService logFilterService,
                              InvalidLogEntryRepository invalidLogEntryRepository) {
        this.logFilterService = logFilterService;
        this.invalidLogEntryRepository = invalidLogEntryRepository;
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredLogs(
            @RequestParam(name = "method", required = false) String method,
            @RequestParam(name = "statusCode", required = false) Integer statusCode,
            @RequestParam(name = "path", required = false) String path,
            @RequestParam(name = "cacheHit", required = false) Boolean cacheHit,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            Pageable pageable) {
        try {
            Instant startInstant = startTime != null ? Instant.parse(startTime) : null;
            Instant endInstant = endTime != null ? Instant.parse(endTime) : null;
            Page<LogEntry> result = logFilterService.filterLogs(
                    method, statusCode, path, cacheHit, startInstant, endInstant, pageable);
            return ResponseEntity.ok(result);
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid date format. Use ISO 8601 (e.g., 2024-01-15T10:23:45.123Z)"));
        }
    }

    @GetMapping("/invalid")
    public ResponseEntity<Page<InvalidLogEntry>> getInvalidLogs(Pageable pageable) {
        return ResponseEntity.ok(invalidLogEntryRepository.findAll(pageable));
    }
}
