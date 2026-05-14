package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayHereInitResponse {

    private String merchantId;

    private String orderId;

    private String amount;

    private String currency;

    private String hash;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String city;

    private String country;
}