package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByFeaturedTrue();

    List<Product> findByCategory(Category category);
    List<Product> findByStatus(String status);
    Optional<Product> findBySku(String sku);
}