package com.example.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing details about the error")
public class ErrorResponseDTO {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Validation failed")
    private String message;

    @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Field-specific validation errors (only for 400 Bad Request)", example = "{\"email\": \"Email must be valid\", \"name\": \"Name is required\"}")
    private Map<String, String> validationErrors;

    public ErrorResponseDTO(int status, String error, String message, LocalDateTime timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }
}
