package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SalesDataPoint {
    private LocalDate date;
    private BigDecimal revenue;
    private Integer orders;
}
