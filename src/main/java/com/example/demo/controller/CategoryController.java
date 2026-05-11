package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDiscountRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.CategoryDiscount;
import com.example.demo.entity.Product;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // GET /api/categories
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategoryResponses();
    }

    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    // GET /api/categories/{id}/products
    @GetMapping("/{id}/products")
    public List<Product> getProductsByCategory(@PathVariable Integer id) {
        return categoryService.getProductsByCategory(id);
    }
    @PostMapping
    public CategoryDiscount create(@RequestBody CategoryDiscountRequest req) {
        return categoryService.create(req);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id,
                                   @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return "Category deleted successfully";
    }
}