package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDiscountRequest;
import com.example.demo.entity.CategoryDiscount;
import com.example.demo.service.CategoryDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/category-discounts")
@RequiredArgsConstructor
public class CategoryDiscountController {

    private final CategoryDiscountService service;

    @PostMapping
    public CategoryDiscount create(@RequestBody CategoryDiscountRequest req) {
        return service.create(
                req.categoryId,
                req.discountType,
                req.value,
                req.startDate,
                req.endDate
        );
    }

    @GetMapping
    public List<CategoryDiscount> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
    @PutMapping("/{id}")
    public CategoryDiscount update(@PathVariable Integer id,
                                   @RequestBody CategoryDiscountRequest req) {
        return service.update(id, req);
    }
}