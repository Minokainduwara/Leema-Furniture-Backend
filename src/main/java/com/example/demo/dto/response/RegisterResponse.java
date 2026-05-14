package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private Integer userId;
    private String email;
    private String message;
}