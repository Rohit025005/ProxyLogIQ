package com.example.proxlogiq.service;

import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.repository.LogEntryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogFilterService {

    private final LogEntryRepository logEntryRepository;

    public LogFilterService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public Page<LogEntry> filterLogs(String method, Integer statusCode, String path,
                                     Boolean cacheHit, Instant startTime, Instant endTime,
                                     Pageable pageable) {
        List<LogEntry> all = logEntryRepository.findAll();
        List<LogEntry> filtered = all.stream()
                .filter(e -> method == null || e.getMethod().equalsIgnoreCase(method))
                .filter(e -> statusCode == null || e.getStatusCode().equals(statusCode))
                .filter(e -> path == null || e.getPath().contains(path))
                .filter(e -> cacheHit == null || e.getCacheHit().equals(cacheHit))
                .filter(e -> startTime == null || !e.getTimestamp().isBefore(startTime))
                .filter(e -> endTime == null || !e.getTimestamp().isAfter(endTime))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<LogEntry> pageContent = start >= filtered.size() ? List.of() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }
}
