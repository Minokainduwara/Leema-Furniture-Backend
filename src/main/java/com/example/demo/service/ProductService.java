package com.example.demo.service;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductUpdateRequest;
import com.example.demo.entity.*;
import com.example.demo.entity.Product.ProductStatus;
import com.example.demo.entity.ProductDiscount;
import com.example.demo.enums.DiscountType;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private CategoryDiscountRepository categoryDiscountRepository;

    // =========================
    // GET ALL PRODUCTS
    // =========================
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    // =========================
    // CREATE PRODUCT (NO DISCOUNT HERE)
    // =========================
    public Product createProduct(
            String name,
            String sku,
            BigDecimal price,
            BigDecimal cost,
            Integer stock,
            String description,
            String longDescription,
            String status,
            String type,
            Integer categoryId,
            MultipartFile image
    ) {

        // validate 4 digits
        if (!sku.matches("\\d{4}")) {
            throw new RuntimeException("SKU must be exactly 4 digits");
        }

// build final SKU
        String finalSku = "AD" + sku;

// check duplicate
        if (productRepository.findBySku(finalSku).isPresent()) {
            throw new RuntimeException("SKU already exists");
        }

        String imagePath = fileService.save(image);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (type == null || type.isBlank()) {
            throw new RuntimeException("Product type is required");
        }

        Product product = new Product();
        product.setName(name);
        product.setSku(finalSku);
        product.setPrice(price);
        product.setCost(cost);
        product.setStock(stock);
        product.setDescription(description);
        product.setLongDescription(longDescription);
        product.setStatus(ProductStatus.valueOf(status.toUpperCase()));
        product.setType(Product.ProductType.valueOf(type.toUpperCase()));
        product.setImage(imagePath);
        product.setCategory(category);
        if (product.getType() == Product.ProductType.TEKA) {
            product.setWarrantyYears(2);
        } else {
            product.setWarrantyYears(15);
        }
        product.setCreatedAt(LocalDateTime.now());
        product.setFeatured(false);
        product.setRating(BigDecimal.ZERO);
        product.setTotalSales(0);

        return productRepository.save(product);
    }

    // =========================
    // UPDATE PRODUCT
    // =========================
    public Product updateProduct(
            Integer id,
            ProductUpdateRequest data,
            MultipartFile image
    ) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (data.getName() != null) product.setName(data.getName());
        if (data.getSku() != null) product.setSku(data.getSku());
        if (data.getPrice() != null) product.setPrice(data.getPrice());
        if (data.getCost() != null) product.setCost(data.getCost());
        if (data.getStock() != null) product.setStock(data.getStock());
        if (data.getDescription() != null) product.setDescription(data.getDescription());
        if (data.getLongDescription() != null)
            product.setLongDescription(data.getLongDescription());

        if (data.getStatus() != null) {
            product.setStatus(data.getStatus());
        }
        if (data.getType() != null) {
            product.setType(data.getType());
        }
        if (product.getType() == Product.ProductType.TEKA) {
            product.setWarrantyYears(2);
        } else {
            product.setWarrantyYears(15);
        }
        if (data.getCategoryId() != null) {
            Category category = categoryRepository.findById(data.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            product.setImage("/uploads/" + fileName);
        }

        return productRepository.save(product);
    }

    // =========================
    // DELETE PRODUCT (ALSO DELETE DISCOUNT)
    // =========================
    @Transactional
    public void deleteProduct(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productDiscountRepository.deleteByProductId(id);

        productRepository.delete(product);
    }

    // =========================
    // DISCOUNT UPSERT (CREATE OR UPDATE)
    // =========================
    @Transactional
    public ProductDiscount upsertDiscount(
            Integer productId,
            String discountType,
            BigDecimal value,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductDiscount discount = productDiscountRepository
                .findByProductId(productId)
                .orElse(new ProductDiscount());

        discount.setProduct(product);
        discount.setDiscountType(DiscountType.valueOf(discountType));
        discount.setValue(value);
        discount.setStartDate(startDate != null ? startDate : LocalDateTime.now());
        discount.setEndDate(endDate);

        return productDiscountRepository.save(discount);
    }

    // =========================
    // GET DISCOUNT BY PRODUCT
    // =========================
    public ProductDiscount getDiscount(Integer productId) {
        return productDiscountRepository.findByProductId(productId)
                .orElse(null);
    }

    // =========================
    // DELETE DISCOUNT
    // =========================
    public void deleteDiscount(Integer productId) {
        productDiscountRepository.deleteByProductId(productId);
    }

    // =========================
    // OTHER FEATURES
    // =========================
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ProductResponse> getRelatedProducts(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return productRepository.findByCategory(product.getCategory())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Product updateStatus(Integer id, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(ProductStatus.valueOf(status.toUpperCase()));
        return productRepository.save(product);
    }

    // =========================
    // RESPONSE MAPPING
    // =========================
    public ProductResponse mapToResponse(Product product) {

        BigDecimal finalPrice = pricingService.calculateFinalPrice(product);

        ProductDiscount pd = productDiscountRepository
                .findActiveDiscount(product.getId(), LocalDateTime.now());

        String type = null;
        BigDecimal value = null;

        if (pd != null) {
            type = pd.getDiscountType().name();
            value = pd.getValue();
        } else {
            CategoryDiscount cd = categoryDiscountRepository
                    .findActiveDiscount(product.getCategory().getId(), LocalDateTime.now());

            if (cd != null) {
                type = cd.getDiscountType().name();
                value = cd.getValue();
            }
        }

        String productType = product.getType() != null
                ? product.getType().name()
                : null;

        if (product.getWarrantyYears() == null) {
            if (product.getType() == Product.ProductType.TEKA) {
                product.setWarrantyYears(2);
            } else {
                product.setWarrantyYears(15);
            }
        }

        ProductResponse response = new ProductResponse(product, type, value, finalPrice, productType, product.getWarrantyYears());




        return response;
    }
}