package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUpdateOrderStatusRequest {
    @NotBlank
    private String status;
    private String note;
}
