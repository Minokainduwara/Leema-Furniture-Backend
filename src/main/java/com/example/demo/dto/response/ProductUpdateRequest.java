package com.example.demo.dto.response;

import com.example.demo.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public void setStatus(Product.ProductStatus status) {
        this.status = status;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Product.ProductStatus getStatus() {
        return status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getImage() {
        return image;
    }
}