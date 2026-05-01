package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private String image;

    private String slug;

    private Boolean isActive = true;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}