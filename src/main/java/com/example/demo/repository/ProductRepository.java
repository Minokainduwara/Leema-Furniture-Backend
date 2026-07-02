package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByFeaturedTrue();

    List<Product> findByCategory(Category category);
    List<Product> findByStatus(Product.ProductStatus status);
    Optional<Product> findBySku(String sku);
    long countByCategory_Id(Integer categoryId);
    long countByDeletedAtIsNullAndStatus(Product.ProductStatus status);
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    Long countInStock();
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < 5")
    Long countLowStock();
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();
}