package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class InventorySummery {
    private Long totalProducts;
    private Long inStock;
    private Long lowStock;
}
