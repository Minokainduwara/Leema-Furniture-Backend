package com.example.demo.dto.response;
import com.example.demo.entity.Product;
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

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();

        // Category (SAFE)
        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
            this.categoryName = product.getCategory().getName();
        }



        // Pricing (SAFE)
        this.price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
        this.cost = product.getCost();

        // Inventory (SAFE)
        this.stock = product.getStock() != null ? product.getStock() : 0;
        this.sku = product.getSku();

        // Content
        this.description = product.getDescription();
        this.longDescription = product.getLongDescription();
        this.image = product.getImage();

        // Status (SAFE)
        this.status = product.getStatus() != null ? product.getStatus().name() : null;

        // Stats (SAFE — THIS IS COMMON CRASH POINT)
        this.rating = product.getRating() != null ? product.getRating() : BigDecimal.ZERO;
        this.totalSales = product.getTotalSales() != null ? product.getTotalSales() : 0;

        // Dates (SAFE)
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
    }

