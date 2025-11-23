package com.example.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error response containing field-specific errors")
public class ValidationErrorDTO {

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Field-specific validation errors",
            example = "{\"email\": \"Email must be valid\", \"name\": \"Name is required\"}")
    private Map<String, String> validationErrors;
}
