package com.example.proxlogiq.dto;

/**
 * DTO for log file upload response.
 */
public class LogUploadResponseDto {

    private int validCount;
    private int invalidCount;
    private String message;

    // Constructors
    public LogUploadResponseDto() {}

    public LogUploadResponseDto(int validCount, int invalidCount, String message) {
        this.validCount = validCount;
        this.invalidCount = invalidCount;
        this.message = message;
    }

    // Getters and Setters
    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount) {
        this.invalidCount = invalidCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}