package com.example.demo.service;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.AdminLog;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminLogRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AdminLogRepository adminLogRepository;
    private final SecurityUtils securityUtils;

    // ─────────────────────────────────────────────

    public Page<ProductResponse> getAllProducts(String status, Integer categoryId,
                                                Pageable pageable) {
        return productRepository.findWithAdminFilters(Product.ProductStatus.valueOf(status), categoryId, pageable)
                .map(this::toResponse);
    }

    public ProductResponse getProduct(Integer id) {
        return toResponse(findProduct(id));
    }

    // ─────────────────────────────────────────────

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // ✅ SKU validation
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        Product product = Product.builder()
                .name(request.getName())
                .category(category)
                .price(request.getPrice())
                .cost(request.getCost())
                .stock(request.getStock() != null ? request.getStock() : 0) // ✅ null safe
                .sku(request.getSku())
                .description(request.getDescription())
                .longDescription(request.getLongDescription())
                .image(request.getImage())
                .status(Product.ProductStatus.ACTIVE) // ✅ assumes ENUM is uppercase
                .build();

        productRepository.save(product);

        var admin = securityUtils.getCurrentUser();

        adminLogRepository.save(AdminLog.builder()
                .admin(admin)
                .action("CREATE")
                .entityType("PRODUCT")
                .entityId(product.getId())
                .newValues(Map.of(
                        "name", product.getName(),
                        "price", product.getPrice(),
                        "stock", product.getStock()
                ).toString())
                .build());

        return toResponse(product);
    }

    // ─────────────────────────────────────────────

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {

        Product product = findProduct(id);
        var admin = securityUtils.getCurrentUser();

        Map<String, Object> old = Map.of(
                "name", product.getName(),
                "price", product.getPrice(),
                "stock", product.getStock()
        );

        // ✅ SKU duplicate check
        if (request.getSku() != null &&
                !request.getSku().equals(product.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        if (request.getName() != null) product.setName(request.getName());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCost() != null) product.setCost(request.getCost());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getLongDescription() != null) product.setLongDescription(request.getLongDescription());
        if (request.getImage() != null) product.setImage(request.getImage());

        productRepository.save(product);

        adminLogRepository.save(AdminLog.builder()
                .admin(admin)
                .action("UPDATE")
                .entityType("PRODUCT")
                .entityId(id)
                .oldValues(old.toString())
                .newValues(Map.of(
                        "name", product.getName(),
                        "price", product.getPrice(),
                        "stock", product.getStock()
                ).toString())
                .build());

        return toResponse(product);
    }

    // ─────────────────────────────────────────────

    @Transactional
    public ProductResponse updateProductStatus(Integer id, String status) {

        Product product = findProduct(id);
        var admin = securityUtils.getCurrentUser();

        String oldStatus = product.getStatus().name();

        // ✅ safe enum conversion
        Product.ProductStatus newStatus;
        try {
            newStatus = Product.ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        product.setStatus(newStatus);
        productRepository.save(product);

        adminLogRepository.save(AdminLog.builder()
                .admin(admin)
                .action("UPDATE_STATUS")
                .entityType("PRODUCT")
                .entityId(id)
                .oldValues(Map.of("status", oldStatus).toString())
                .newValues(Map.of("status", newStatus.name()).toString())
                .build());

        return toResponse(product);
    }

    // ─────────────────────────────────────────────

    @Transactional
    public void deleteProduct(Integer id) {

        Product product = findProduct(id);
        var admin = securityUtils.getCurrentUser();

        product.setStatus(Product.ProductStatus.DISCONTINUED); // ✅ consistent
        product.setDeletedAt(LocalDateTime.now());

        productRepository.save(product);

        adminLogRepository.save(AdminLog.builder()
                .admin(admin)
                .action("DELETE")
                .entityType("PRODUCT")
                .entityId(id)
                .oldValues(Map.of("status", product.getStatus().name()).toString())
                .newValues(Map.of("status", "DISCONTINUED").toString())
                .build());
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Product findProduct(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .price(p.getPrice())
                .cost(p.getCost())
                .stock(p.getStock())
                .sku(p.getSku())
                .description(p.getDescription())
                .image(p.getImage())
                .status(p.getStatus().name())
                .rating(p.getRating())
                .totalSales(p.getTotalSales())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}