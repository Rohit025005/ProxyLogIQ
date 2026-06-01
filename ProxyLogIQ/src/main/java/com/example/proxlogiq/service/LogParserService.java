package com.example.proxlogiq.service;

import com.example.proxlogiq.entity.InvalidLogEntry;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.exception.LogParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for parsing pipe-delimited proxy log files.
 * Processes files line-by-line to avoid memory issues with large files.
 */
@Service
public class LogParserService {

    /**
     * Parses a log file and returns valid and invalid entries.
     *
     * @param file the log file to parse
     * @return a pair of lists containing valid entries and invalid entries with error reasons
     * @throws IOException if there's an error reading the file
     */
    public ParsedLogResult parseLogFile(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return parseLogFile(is);
        }
    }

    public ParsedLogResult parseLogFile(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return parseLines(reader);
        }
    }

    public ParsedLogResult parseLogFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseLines(reader);
        }
    }

    private ParsedLogResult parseLines(BufferedReader reader) throws IOException {
        List<LogEntry> validEntries = new ArrayList<>();
        List<InvalidLogEntry> invalidEntries = new ArrayList<>();

        String line;
        long lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Skip empty lines and comments
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                LogEntry entry = parseLogLine(line);
                if (entry != null) {
                    validEntries.add(entry);
                } else {
                    invalidEntries.add(new InvalidLogEntry(
                            line, "Unknown parsing error"));
                }
            } catch (LogParseException e) {
                invalidEntries.add(new InvalidLogEntry(line, e.getMessage()));
            }
        }

        return new ParsedLogResult(validEntries, invalidEntries);
    }

    /**
     * Parses a single log line into a LogEntry object.
     *
     * @param line the log line to parse (pipe-delimited)
     * @return the parsed LogEntry
     * @throws LogParseException if the line cannot be parsed
     */
    LogEntry parseLogLine(String line) throws LogParseException {
        String[] fields = line.split("\\|", -1); // -1 to keep trailing empty strings

        // Expect exactly 7 fields: timestamp | method | path | status_code | cache_status | latency_ms | bytes
        if (fields.length != 7) {
            throw new LogParseException(
                    "Expected 7 fields but found " + fields.length + ": " + line);
        }

        // Trim whitespace from each field
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        try {
            // Parse timestamp (ISO 8601 format: 2024-01-15T10:23:45.123Z)
            Instant timestamp = Instant.parse(fields[0]);

            // Parse method (GET, POST, PUT, DELETE, PATCH, etc.)
            String method = fields[1];
            if (method.isEmpty()) {
                throw new LogParseException("Method cannot be empty");
            }

            // Parse path
            String path = fields[2];
            if (path.isEmpty()) {
                throw new LogParseException("Path cannot be empty");
            }

            // Parse status code
            int statusCode = Integer.parseInt(fields[3]);
            if (statusCode < 100 || statusCode > 599) {
                throw new LogParseException("Invalid status code: " + statusCode);
            }

            // Parse cache status (HIT or MISS)
            String cacheStatus = fields[4].toUpperCase();
            boolean cacheHit;
            switch (cacheStatus) {
                case "HIT":
                    cacheHit = true;
                    break;
                case "MISS":
                    cacheHit = false;
                    break;
                default:
                    throw new LogParseException("Invalid cache status: " + cacheStatus +
                            ". Must be 'HIT' or 'MISS'");
            }

            // Parse latency (milliseconds)
            long latencyMs = Long.parseLong(fields[5]);
            if (latencyMs < 0) {
                throw new LogParseException("Latency cannot be negative: " + latencyMs);
            }

            // Parse bytes
            long bytes = Long.parseLong(fields[6]);
            if (bytes < 0) {
                throw new LogParseException("Bytes cannot be negative: " + bytes);
            }

            return new LogEntry(timestamp, method, path, statusCode, cacheHit, latencyMs, bytes);

        } catch (NumberFormatException e) {
            throw new LogParseException("Invalid number format in line: " + line);
        } catch (DateTimeParseException e) {
            throw new LogParseException("Invalid timestamp format: " + fields[0] +
                    ". Expected ISO 8601 format (e.g., 2024-01-15T10:23:45.123Z)");
        }
    }

    /**
     * Result of parsing a log file containing both valid and invalid entries.
     */
    public static class ParsedLogResult {
        private final List<LogEntry> validEntries;
        private final List<InvalidLogEntry> invalidEntries;

        public ParsedLogResult(List<LogEntry> validEntries, List<InvalidLogEntry> invalidEntries) {
            this.validEntries = validEntries;
            this.invalidEntries = invalidEntries;
        }

        public List<LogEntry> getValidEntries() {
            return validEntries;
        }

        public List<InvalidLogEntry> getInvalidEntries() {
            return invalidEntries;
        }

        public int getValidCount() {
            return validEntries.size();
        }

        public int getInvalidCount() {
            return invalidEntries.size();
        }
    }
}