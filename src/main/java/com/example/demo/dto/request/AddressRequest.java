package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "fullName is required")
        @Size(max = 255, message = "fullName must be at most 255 characters")
        String fullName,

        @NotBlank(message = "phoneNumber is required")
        @Size(max = 20, message = "phoneNumber must be at most 20 characters")
        String phoneNumber,

        @Email(message = "email must be valid")
        @Size(max = 255, message = "email must be at most 255 characters")
        String email,

        @NotBlank(message = "streetAddress is required")
        @Size(max = 255, message = "streetAddress must be at most 255 characters")
        String streetAddress,

        @Size(max = 255, message = "apartmentSuite must be at most 255 characters")
        String apartmentSuite,

        @NotBlank(message = "city is required")
        @Size(max = 100, message = "city must be at most 100 characters")
        String city,

        @NotBlank(message = "stateProvince is required")
        @Size(max = 100, message = "stateProvince must be at most 100 characters")
        String stateProvince,

        @NotBlank(message = "postalCode is required")
        @Size(max = 20, message = "postalCode must be at most 20 characters")
        String postalCode,

        @NotBlank(message = "country is required")
        @Size(max = 100, message = "country must be at most 100 characters")
        String country,

        Boolean isDefault

) {}