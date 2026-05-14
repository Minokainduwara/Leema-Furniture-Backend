package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminInventoryAlertRequest {
    @NotNull
    @Min(0) private Integer thresholdQuantity;
}
