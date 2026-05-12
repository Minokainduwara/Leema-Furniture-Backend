package com.example.demo.controller;

import com.example.demo.dto.request.ProductDiscountRequest;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.service.ProductDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-discounts")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ProductDiscountController {

    private final ProductDiscountService service;
    @GetMapping("/product/{productId}")
    public ProductDiscount getByProduct(@PathVariable Integer productId) {
        return service.getByProductId(productId);
    }
    @PostMapping
    public ProductDiscount create(@RequestBody ProductDiscountRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<ProductDiscount> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/product/{productId}")
    public void deleteByProductId(@PathVariable Integer productId) {
        service.deleteByProductId(productId);
    }
}