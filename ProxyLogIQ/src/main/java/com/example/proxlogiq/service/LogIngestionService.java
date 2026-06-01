package com.example.proxlogiq.service;

import com.example.proxlogiq.entity.InvalidLogEntry;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.repository.InvalidLogEntryRepository;
import com.example.proxlogiq.repository.LogEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class LogIngestionService {

    private final LogParserService logParserService;
    private final LogEntryRepository logEntryRepository;
    private final InvalidLogEntryRepository invalidLogEntryRepository;

    public LogIngestionService(LogParserService logParserService,
                               LogEntryRepository logEntryRepository,
                               InvalidLogEntryRepository invalidLogEntryRepository) {
        this.logParserService = logParserService;
        this.logEntryRepository = logEntryRepository;
        this.invalidLogEntryRepository = invalidLogEntryRepository;
    }

    @Transactional
    public IngestionResult ingest(MultipartFile file) throws IOException {
        LogParserService.ParsedLogResult result = logParserService.parseLogFile(file);

        logEntryRepository.saveAll(result.getValidEntries());
        invalidLogEntryRepository.saveAll(result.getInvalidEntries());

        return new IngestionResult(result.getValidCount(), result.getInvalidCount());
    }

    public record IngestionResult(int validCount, int invalidCount) {}
}
