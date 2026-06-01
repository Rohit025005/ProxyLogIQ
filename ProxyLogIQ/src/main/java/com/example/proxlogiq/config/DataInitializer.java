package com.example.proxlogiq.config;

import com.example.proxlogiq.repository.InvalidLogEntryRepository;
import com.example.proxlogiq.repository.LogEntryRepository;
import com.example.proxlogiq.service.LogParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;

@Component
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final LogParserService logParserService;
    private final LogEntryRepository logEntryRepository;
    private final InvalidLogEntryRepository invalidLogEntryRepository;

    public DataInitializer(LogParserService logParserService,
                           LogEntryRepository logEntryRepository,
                           InvalidLogEntryRepository invalidLogEntryRepository) {
        this.logParserService = logParserService;
        this.logEntryRepository = logEntryRepository;
        this.invalidLogEntryRepository = invalidLogEntryRepository;
    }

    @PostConstruct
    void loadSampleData() throws IOException {
        if (logEntryRepository.count() > 0) {
            log.info("Database already contains data, skipping sample data load");
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/sample.log");
        if (!resource.exists()) {
            log.warn("sample.log not found, skipping data load");
            return;
        }

        log.info("Loading sample data from data/sample.log...");
        Path filePath = resource.getFile().toPath();
        LogParserService.ParsedLogResult result = logParserService.parseLogFile(filePath);

        logEntryRepository.saveAll(result.getValidEntries());
        invalidLogEntryRepository.saveAll(result.getInvalidEntries());

        log.info("Loaded {} valid log entries and {} invalid entries",
                result.getValidCount(), result.getInvalidCount());
    }
}
