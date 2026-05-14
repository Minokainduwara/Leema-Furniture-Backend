package com.example.demo.service;

import com.example.demo.dto.request.CategoryDiscountRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.CategoryDiscount;
import com.example.demo.entity.Product;
import com.example.demo.enums.DiscountType;
import com.example.demo.repository.CategoryDiscountRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryDiscountRepository categoryDiscountRepository;

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }
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
    public CategoryDiscount create(CategoryDiscountRequest req) {

        Category category = categoryRepository.findById(req.categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        CategoryDiscount discount = new CategoryDiscount();
        discount.setCategory(category);
        discount.setDiscountType(DiscountType.valueOf(req.discountType));
        discount.setValue(req.value);
        discount.setStartDate(req.startDate);
        discount.setEndDate(req.endDate);
        discount.setActive(true);

        return categoryDiscountRepository.save(discount);
    }

    // ===================== UPDATE CATEGORY =====================
    public Category updateCategory(Integer id, Category updatedCategory) {

        Category existingCategory = getCategoryById(id);

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setSlug(updatedCategory.getSlug());
        existingCategory.setIsActive(updatedCategory.getIsActive());

        return categoryRepository.save(existingCategory);
    }

    // ===================== DELETE CATEGORY =====================
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
    public List<CategoryResponse> getAllCategoryResponses() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> {

            CategoryDiscount discount =
                    categoryDiscountRepository.findActiveDiscount(
                            category.getId(),
                            LocalDateTime.now()
                    );

            CategoryResponse res = new CategoryResponse();

            res.setId(category.getId());
            res.setName(category.getName());
            res.setDescription(category.getDescription());
            res.setSlug(category.getSlug());
            res.setActive(category.getIsActive());

            if (discount != null) {
                res.setDiscountType(discount.getDiscountType().name());
                res.setDiscountValue(discount.getValue());
            }
            res.setDiscountedProductsCount(
                    productRepository.countByCategory_Id(category.getId())
            );

            return res;
        }).toList();
    }
}