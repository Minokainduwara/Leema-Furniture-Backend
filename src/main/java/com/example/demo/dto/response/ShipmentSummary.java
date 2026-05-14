package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentSummary {
    private Integer id;
    private String trackingNumber;
    private String carrier;
    private String status;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
}