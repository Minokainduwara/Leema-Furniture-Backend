package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminInventoryAdjustRequest {
    @NotNull
    private Integer quantityChange;
    @NotBlank
    private String reason;
    private String notes;
}