package com.example.proxlogiq.controller;

import com.example.proxlogiq.dto.LogUploadResponseDto;
import com.example.proxlogiq.service.LogIngestionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogUploadController {

    private final LogIngestionService logIngestionService;

    public LogUploadController(LogIngestionService logIngestionService) {
        this.logIngestionService = logIngestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadLogFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "File is empty"));
        }
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".log") && !filename.endsWith(".txt"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid file type. Only .log and .txt files are allowed"));
        }
        try {
            LogIngestionService.IngestionResult result = logIngestionService.ingest(file);
            return ResponseEntity.ok(new LogUploadResponseDto(
                    result.validCount(), result.invalidCount(), "File processed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error processing file: " + e.getMessage()));
        }
    }
}
