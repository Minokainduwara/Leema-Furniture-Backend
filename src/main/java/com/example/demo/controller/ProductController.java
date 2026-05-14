package com.example.demo.controller;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductUpdateRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Product.ProductStatus;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // =====================================
    // GET ALL PRODUCTS
    // =====================================
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // =====================================
    // GET PRODUCT BY ID
    // =====================================
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    // =====================================
    // CREATE PRODUCT (NO DISCOUNT LOGIC HERE)
    // Discount handled separately via ProductDiscountController
    // =====================================
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam String sku,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal cost,
            @RequestParam Integer stock,
            @RequestParam String description,
            @RequestParam String longDescription,
            @RequestParam String status,
            @RequestParam Integer categoryId,
            @RequestParam(required = false) MultipartFile image
    ) {

        Product product = productService.createProduct(
                name,
                sku,
                price,
                cost,// discountType removed (handled separately) // discountValue removed
                stock,
                description,
                longDescription,
                status,
                categoryId,
                image
        );

        return ResponseEntity.ok(product.getId());
    }

    // =====================================
    // UPDATE PRODUCT
    // (Discount is NOT updated here - use ProductDiscountController)
    // =====================================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product updateProduct(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String sku,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal cost,
            @RequestParam Integer stock,
            @RequestParam String description,
            @RequestParam String longDescription,
            @RequestParam String status,
            @RequestParam Integer categoryId,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {

        ProductUpdateRequest data = new ProductUpdateRequest();

        data.setName(name);
        data.setSku(sku);
        data.setPrice(price);
        data.setCost(cost);
        data.setStock(stock);
        data.setDescription(description);
        data.setLongDescription(longDescription);
        data.setStatus(ProductStatus.valueOf(status.toUpperCase()));
        data.setCategoryId(categoryId);

        return productService.updateProduct(id, data, image);
    }

    // =====================================
    // DELETE PRODUCT (also deletes discount in service)
    // =====================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // =====================================
    // SEARCH PRODUCTS
    // =====================================
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // =====================================
    // FEATURED PRODUCTS
    // =====================================
    @GetMapping("/featured")
    public List<ProductResponse> getFeaturedProducts() {
        return productService.getFeaturedProducts();
    }

    // =====================================
    // RELATED PRODUCTS
    // =====================================
    @GetMapping("/{id}/related")
    public List<ProductResponse> getRelatedProducts(@PathVariable Integer id) {
        return productService.getRelatedProducts(id);
    }

    // =====================================
    // UPDATE PRODUCT STATUS
    // =====================================
    @PatchMapping("/{id}/status")
    public ProductResponse updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        Product updated = productService.updateStatus(id, body.get("status"));
        return productService.mapToResponse(updated);
    }
}