package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminRejectSellerRequest {
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}
