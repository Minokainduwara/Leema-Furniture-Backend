package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String email;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String apartmentSuite;

    @NotBlank(message = "City is required")
    private String city;

    private String stateProvince;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String customerNotes;
}