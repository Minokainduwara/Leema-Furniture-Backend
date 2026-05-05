package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdateUserStatusRequest {
    @NotBlank
    private String status; // active, suspended, deleted
    private String reason;
}
