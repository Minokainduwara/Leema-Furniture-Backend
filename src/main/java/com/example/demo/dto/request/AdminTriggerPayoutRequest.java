package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminTriggerPayoutRequest {
    @NotNull
    private BigDecimal amount;
    private String payoutMethod;
    private String notes;
    private String periodFrom;
    private String periodTo;
}
