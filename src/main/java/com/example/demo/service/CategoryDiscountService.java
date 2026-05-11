package com.example.demo.service;

import com.example.demo.dto.request.CategoryDiscountRequest;
import com.example.demo.entity.Category;
import com.example.demo.entity.CategoryDiscount;
import com.example.demo.enums.DiscountType;
import com.example.demo.repository.CategoryDiscountRepository;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryDiscountService {
    @Autowired
    private final CategoryDiscountRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDiscount create(
            Integer categoryId,
            String discountType,
            BigDecimal value,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        CategoryDiscount discount = repository
                .findByCategoryId(categoryId)
                .orElse(new CategoryDiscount());

        discount.setCategory(category);
        discount.setDiscountType(DiscountType.valueOf(discountType));
        discount.setValue(value);
        discount.setStartDate(startDate != null ? startDate : LocalDateTime.now());
        discount.setEndDate(endDate);

        return repository.save(discount);
    }

    public List<CategoryDiscount> getAll() {
        return repository.findAll();
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public CategoryDiscount update(Integer id, CategoryDiscountRequest req) {

        CategoryDiscount discount = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        discount.setDiscountType(DiscountType.valueOf(req.discountType));
        discount.setValue(req.value);
        discount.setStartDate(req.startDate);
        discount.setEndDate(req.endDate);

        return repository.save(discount);
    }
}