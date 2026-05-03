package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record AddressResponse(
        Integer id,
        Integer userId,
        String fullName,
        String phoneNumber,
        String email,
        String streetAddress,
        String apartmentSuite,
        String city,
        String stateProvince,
        String postalCode,
        String country,
        Boolean isDefault,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}