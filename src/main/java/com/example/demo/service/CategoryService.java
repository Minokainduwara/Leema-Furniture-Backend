package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // ===================== GET ALL CATEGORIES =====================
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ===================== GET CATEGORY BY ID =====================
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    // ===================== GET PRODUCTS BY CATEGORY =====================
    public List<Product> getProductsByCategory(Integer id) {
        Category category = getCategoryById(id);
        return productRepository.findByCategory(category);
    }

    // ===================== CREATE CATEGORY =====================
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // ===================== UPDATE CATEGORY =====================
    public Category updateCategory(Integer id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setImage(updatedCategory.getImage());
        existingCategory.setSlug(updatedCategory.getSlug());
        existingCategory.setIsActive(updatedCategory.getIsActive());

        return categoryRepository.save(existingCategory);
    }

    // ===================== DELETE CATEGORY =====================
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}