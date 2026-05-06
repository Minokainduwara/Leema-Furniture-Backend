package com.example.demo.service;

import com.example.demo.dto.response.ProductUpdateRequest;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.Product.ProductStatus;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
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

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElseThrow();
    }

    // CREATE product
    public void createProduct(
            String name,
            String sku,
            BigDecimal price,
            BigDecimal cost,
            Integer stock,
            String description,
            String longDescription,
            String status,
            Integer categoryId,
            MultipartFile image
    ) {
        if (productRepository.findBySku(sku).isPresent()) {
            throw new RuntimeException("SKU already exists");
        }
        // save image
        String imagePath = fileService.save(image);

        //  get category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        //  create product
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(price);
        product.setCost(cost);
        product.setStock(stock);
        product.setDescription(description);
        product.setLongDescription(longDescription);
        product.setStatus(ProductStatus.valueOf(status));
        product.setImage(imagePath);
        product.setCategory(category);

        product.setCreatedAt(LocalDateTime.now());
        product.setFeatured(false);
        product.setRating(new BigDecimal("0.00"));
        product.setTotalSales(0);

        productRepository.save(product);
    }

    // UPDATE product
    public Product updateProduct(
            Integer id,
            ProductUpdateRequest data,
            MultipartFile image
    ) throws IOException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ✅ FIX: use getters
        if (data.getName() != null) product.setName(data.getName());
        if (data.getSku() != null) product.setSku(data.getSku());
        if (data.getPrice() != null) product.setPrice(data.getPrice());
        if (data.getCost() != null) product.setCost(data.getCost());
        if (data.getStock() != null) product.setStock(data.getStock());
        if (data.getDescription() != null) product.setDescription(data.getDescription());
        if (data.getLongDescription() != null)
            product.setLongDescription(data.getLongDescription());

        // ✅ FIX: safe enum conversion
        if (data.getStatus() != null) {
            product.setStatus(data.getStatus());
        }

        // Category
        if (data.getCategoryId() != null) {
            Category category = categoryRepository.findById(data.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Image upload
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            product.setImage("/uploads/" + fileName);
        }

        return productRepository.save(product);
    }
    // DELETE product
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getRelatedProducts(Integer id) {
        Product product = getProductById(id);
        return productRepository.findByCategory(product.getCategory());
    }
    public Product updateStatus(Integer id, String status) {
        Product product = getProductById(id);
        product.setStatus(ProductStatus.valueOf(status.toUpperCase()));
        return productRepository.save(product);
    }
}