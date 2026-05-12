package com.example.demo.dto.response;

import com.example.demo.entity.Product;
import java.math.BigDecimal;

public class ProductResponse {

    private Integer id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private CategoryDTO category;
    private BigDecimal cost;
    private String description;
    private String longDescription;

    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal finalPrice;

    public ProductResponse(Product product,
                           String discountType,
                           BigDecimal discountValue,
                           BigDecimal finalPrice) {

        this.id = product.getId();
        this.name = product.getName();
        this.sku = product.getSku();
        this.price = product.getPrice();
        this.stock = product.getStock();
        if (product.getCategory() != null) {
            this.category = new CategoryDTO(
                    product.getCategory().getId(),
                    product.getCategory().getName()
            );
        }
        this.cost = product.getCost();
        this.description = product.getDescription();
        this.longDescription = product.getLongDescription();
        this.image = product.getImage();

        this.discountType = discountType;
        this.discountValue = discountValue;
        this.finalPrice = finalPrice;
    }
    public BigDecimal getCost() {
        return cost;
    }



    public String getDescription() {
        return description;
    }



    public String getLongDescription() {
        return longDescription;
    }


    public CategoryDTO getCategory() {
        return category;
    }
    public Integer getId() {
        return id;
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



    public Integer getStock() {
        return stock;
    }



    public String getImage() {
        return image;
    }





    public String getDiscountType() {
        return discountType;
    }



    public BigDecimal getDiscountValue() {
        return discountValue;
    }



    public BigDecimal getFinalPrice() {
        return finalPrice;
    }


}