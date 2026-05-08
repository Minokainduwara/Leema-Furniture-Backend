package com.example.demo.dto.response;

import com.example.demo.entity.Product;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Data
public class ProductUpdateRequest {

    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stock;
    private String description;
    private String longDescription;
    private Product.ProductStatus status;
    private Integer categoryId;
    private String image;

}