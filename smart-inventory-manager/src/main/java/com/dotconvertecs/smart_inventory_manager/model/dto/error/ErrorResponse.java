package com.dotconvertecs.smart_inventory_manager.model.dto.error;

import lombok.Data;
import java.time.LocalDateTime;

// Simple error response payload
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    // Constructors, getters, setters

    public ErrorResponse(LocalDateTime timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }
}
