package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCategoryRequest {
    @NotBlank
    private String name;
    private String description;
    private String image;
    private String slug;
    private boolean isActive = true;
}
