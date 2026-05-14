package com.example.demo.dto.request;

import lombok.Data;

@Data
public class PaymentMethodRequest {

    private String methodType;

    private String cardLastFour;

    private String cardBrand;

    private Integer cardExpiryMonth;

    private Integer cardExpiryYear;

    private String email;

    private Boolean isDefault;
}