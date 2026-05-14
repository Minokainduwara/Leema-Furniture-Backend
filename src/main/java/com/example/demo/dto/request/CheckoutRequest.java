package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CheckoutRequest {

    // SHIPPING ADDRESSS

    private String fullName;

    private String phoneNumber;

    private String email;

    private String streetAddress;

    private String apartmentSuite;

    private String city;

    private String stateProvince;

    private String postalCode;

    private String country;

    // PAYMENT

    private String paymentMethod;

    // NOTES

    private String customerNotes;
}