package com.example.demo.entity;

import com.example.demo.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Double price;

    private Double cost;

    private Integer stock;

    private String sku;

    private String description;

    @Column(name = "long_description", columnDefinition = "LONGTEXT")
    private String longDescription;

    private String image;

    @Column(columnDefinition = "JSON")
    private String images;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private Double rating;

    @Column(name = "total_sales")
    private Integer totalSales;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "featured")
    private Boolean featured = false;
}
