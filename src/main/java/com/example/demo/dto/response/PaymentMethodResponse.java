package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodResponse {

    private Integer id;

    private String methodType;

    private Boolean isDefault;

    private String cardLastFour;

    private String cardBrand;

    private Integer cardExpiryMonth;

    private Integer cardExpiryYear;

    private String email;
}