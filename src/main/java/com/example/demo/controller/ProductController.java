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


    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }


    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }


    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam String skuDigits,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal cost,
            @RequestParam Integer stock,
            @RequestParam String description,
            @RequestParam String longDescription,
            @RequestParam String status,
            @RequestParam String type,
            @RequestParam Integer categoryId,
            @RequestParam(required = false) MultipartFile image
    ) {

        Product product = productService.createProduct(
                name,
                skuDigits,
                price,
                cost,
                stock,
                description,
                longDescription,
                status,
                type,
                categoryId,
                image
        );

        return ResponseEntity.ok(product.getId());
    }


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
            @RequestParam String type,
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
        data.setType(Product.ProductType.valueOf(type.toUpperCase()));
        data.setType(Product.ProductType.valueOf(type.toUpperCase()));
        data.setCategoryId(categoryId);

        return productService.updateProduct(id, data, image);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }


    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }


    @GetMapping("/featured")
    public List<ProductResponse> getFeaturedProducts() {
        return productService.getFeaturedProducts();
    }


    @GetMapping("/{id}/related")
    public List<ProductResponse> getRelatedProducts(@PathVariable Integer id) {
        return productService.getRelatedProducts(id);
    }


    @PatchMapping("/{id}/status")
    public ProductResponse updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body
    ) {
        Product updated = productService.updateStatus(id, body.get("status"));
        return productService.mapToResponse(updated);
    }
}