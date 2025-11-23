package com.example.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Simple error response with a single message")
public class ErrorMessageDTO {

    @Schema(description = "Error message", example = "Resource not found")
    private String message;
}
