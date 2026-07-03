package com.example.demo.repository;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    long count();
    @Query("""
SELECT c.name as category, COUNT(p) as count
FROM Product p
JOIN p.category c
GROUP BY c.name
""")
    List<Map<String, Object>> getCategoryDistribution();
    List<Category> findByIsActiveTrue();
    @Query("""
SELECT c.isActive, COUNT(c)
FROM Category c
GROUP BY c.isActive
""")
    List<Object[]> getCategoryStatusCount();
}