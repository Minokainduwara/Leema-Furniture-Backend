package com.example.demo.dto.response;

import com.example.demo.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {

    private String name;

    private String sku;

    private Double price;

    private Double cost;

    private Integer stock;

    private String description;

    private String longDescription;

    private ProductStatus status;

    private Integer categoryId;
    private String image;
}