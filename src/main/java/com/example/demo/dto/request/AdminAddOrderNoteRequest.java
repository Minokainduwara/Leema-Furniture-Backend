package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminAddOrderNoteRequest {
    @NotBlank
    private String note;
}