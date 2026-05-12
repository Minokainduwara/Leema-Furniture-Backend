package com.example.demo.dto.response;

import java.math.BigDecimal;

public class CategoryResponse {

    private Integer id;
    private String name;
    private String description;
    private String slug;
    private Boolean isActive;

    private String discountType;
    private BigDecimal discountValue;
    private long discountedProductsCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public long getDiscountedProductsCount() {
        return discountedProductsCount;
    }

    public void setDiscountedProductsCount(long discountedProductsCount) {
        this.discountedProductsCount = discountedProductsCount;
    }
}