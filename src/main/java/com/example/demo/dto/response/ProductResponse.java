package com.example.demo.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Integer id;
    private String name;

    // Category
    private Integer categoryId;
    private String categoryName;

    // Seller (null for admin-created products)
    private Integer sellerId;
    private String sellerShopName;

    // Pricing
    private BigDecimal price;
    private BigDecimal cost;           // admin/seller only — hidden from buyers

    // Inventory
    private Integer stock;
    private String sku;

    // Content
    private String description;
    private String longDescription;
    private String image;
    private List<String> images;

    // Status & flags
    private String status;

    // Stats
    private BigDecimal rating;
    private Integer totalSales;
    private Integer reviewCount;

    // Attributes
    private List<ProductAttributeResponse> attributes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeResponse {
        private Integer id;
        private String attributeName;
        private String attributeValue;
    }
}