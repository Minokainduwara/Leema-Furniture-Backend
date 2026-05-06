package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminShippingMethodRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @DecimalMin("0.00") private BigDecimal cost;
    private Integer estimatedDays;
    private Integer estimatedDaysMax;
    private boolean isActive = true;
    private java.util.List<String> countries;
}
