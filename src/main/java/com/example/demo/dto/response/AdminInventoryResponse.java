package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminInventoryResponse {
    private Integer productId;
    private String productName;
    private String sku;
    private String category;
    private Integer stock;
    private Integer threshold;
    private boolean lowStock;
    private LocalDateTime updatedAt;
}
