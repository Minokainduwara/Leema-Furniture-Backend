package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {

    private Integer productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private String category;
    private String productCode;
}
