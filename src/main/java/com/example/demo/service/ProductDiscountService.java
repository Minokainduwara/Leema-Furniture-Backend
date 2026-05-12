package com.example.demo.service;

import com.example.demo.dto.request.ProductDiscountRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.enums.DiscountType;
import com.example.demo.repository.ProductDiscountRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDiscountService {

    private final ProductDiscountRepository repository;
    private final ProductRepository productRepository;

    public ProductDiscount getByProductId(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return repository.findByProduct(product);
    }
    public ProductDiscount create(ProductDiscountRequest req) {

        Product product = productRepository.findById(req.productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ FIND EXISTING
        ProductDiscount discount = repository.findByProduct(product);

        // ✅ IF NOT EXISTS → CREATE NEW
        if (discount == null) {
            discount = new ProductDiscount();
            discount.setProduct(product);
        }

        // ✅ UPDATE VALUES (both new + existing)
        discount.setDiscountType(DiscountType.valueOf(req.discountType));
        discount.setValue(req.value);
        discount.setStartDate(req.startDate);
        discount.setEndDate(req.endDate);

        return repository.save(discount);
    }
    public List<ProductDiscount> getAll() {
        return repository.findAll();
    }

    public void deleteByProductId(Integer productId) {
        repository.deleteByProductId(productId);
    }
}