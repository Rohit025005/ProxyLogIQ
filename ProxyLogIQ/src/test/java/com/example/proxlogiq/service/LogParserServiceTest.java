package com.example.proxlogiq.service;

import com.example.proxlogiq.entity.InvalidLogEntry;
import com.example.proxlogiq.entity.LogEntry;
import com.example.proxlogiq.exception.LogParseException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogParserServiceTest {

    private final LogParserService service = new LogParserService();

    @Test
    void parseValidLine_shouldReturnLogEntry() throws LogParseException {
        String line = "2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12 | 1024";
        LogEntry entry = service.parseLogLine(line);
        assertNotNull(entry);
        assertEquals("GET", entry.getMethod());
        assertEquals("/api/users", entry.getPath());
        assertEquals(200, entry.getStatusCode());
        assertTrue(entry.getCacheHit());
        assertEquals(12L, entry.getLatencyMs());
        assertEquals(1024L, entry.getBytes());
    }

    @Test
    void parseInvalidLine_wrongFieldCount_shouldThrow() {
        String line = "2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12";
        assertThrows(LogParseException.class, () -> service.parseLogLine(line));
    }

    @Test
    void parseInvalidLine_badTimestamp_shouldThrow() {
        String line = "not-a-date | GET | /api/users | 200 | HIT | 12 | 1024";
        assertThrows(LogParseException.class, () -> service.parseLogLine(line));
    }

    @Test
    void parseInvalidLine_negativeLatency_shouldThrow() {
        String line = "2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | -5 | 100";
        assertThrows(LogParseException.class, () -> service.parseLogLine(line));
    }

    @Test
    void parseInvalidLine_badStatusCode_shouldThrow() {
        String line = "2024-01-15T10:23:45.123Z | GET | /api/users | 999 | HIT | 10 | 100";
        assertThrows(LogParseException.class, () -> service.parseLogLine(line));
    }

    @Test
    void parseFile_shouldReturnValidAndInvalid() throws IOException {
        String content = String.join("\n",
                "2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12 | 1024",
                "2024-01-15T10:23:46.456Z | POST | /api/orders | 201 | MISS | 340 | 2048",
                "bad-line",
                "2024-01-15T10:23:47.789Z | GET | /api/products | 200 | HIT | 8 | 512"
        );
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.log", "text/plain",
                new ByteArrayInputStream(content.getBytes())
        );

        LogParserService.ParsedLogResult result = service.parseLogFile(file);

        assertEquals(3, result.getValidCount());
        assertEquals(1, result.getInvalidCount());
        assertEquals("Expected 7 fields but found 1: bad-line", result.getInvalidEntries().get(0).getErrorReason());
    }

    @Test
    void parseFile_emptyAndCommentLines_shouldBeSkipped() throws IOException {
        String content = String.join("\n",
                "# This is a comment",
                "",
                "2024-01-15T10:23:45.123Z | GET | /api/users | 200 | HIT | 12 | 1024",
                "  ",
                "# Another comment"
        );
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.log", "text/plain",
                new ByteArrayInputStream(content.getBytes())
        );

        LogParserService.ParsedLogResult result = service.parseLogFile(file);

        assertEquals(1, result.getValidCount());
        assertEquals(0, result.getInvalidCount());
    }
}
