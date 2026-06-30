package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Integer id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String streetAddress;
    private String apartmentSuite;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private Boolean isDefault;
}
