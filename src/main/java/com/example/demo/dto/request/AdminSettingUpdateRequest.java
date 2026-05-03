package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class AdminSettingUpdateRequest {
    @NotNull
    private Map<String, Object> value;
    private String description;
}