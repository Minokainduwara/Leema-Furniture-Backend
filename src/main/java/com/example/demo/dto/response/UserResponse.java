package com.example.demo.dto.response;

import lombok.Data;

@Data
public class UserResponse {

    private Integer id;
    private String email;
    private String name;
    private String phoneNumber;
    private String role;
}