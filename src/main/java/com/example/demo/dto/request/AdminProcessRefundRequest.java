package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminProcessRefundRequest {
    @NotBlank
    private String action; // approve, reject
    private String notes;
}
