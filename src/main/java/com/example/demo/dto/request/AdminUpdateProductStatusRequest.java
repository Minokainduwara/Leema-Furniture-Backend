package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdateProductStatusRequest {
    @NotBlank
    private String status; // active, inactive, discontinued, draft
}
