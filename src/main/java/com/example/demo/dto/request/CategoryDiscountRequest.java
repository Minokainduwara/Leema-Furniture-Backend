package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CategoryDiscountRequest {

    public Integer categoryId;
    public String discountType;
    public BigDecimal value;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
}