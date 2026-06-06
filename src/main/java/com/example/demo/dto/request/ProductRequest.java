package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal cost;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String sku;
    private String description;
    private String longDescription;
    private String image;
    private List<String> images;
    private String status;             // active, inactive, draft

    // Key-value product attributes (e.g. color, size, weight)
    private List<AttributeRequest> attributes;

    @Data
    public static class AttributeRequest {
        @NotBlank private String attributeName;
        @NotBlank private String attributeValue;
    }
}