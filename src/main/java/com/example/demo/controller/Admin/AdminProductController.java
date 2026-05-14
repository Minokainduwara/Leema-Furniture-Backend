package com.example.demo.controller.Admin;

import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductUpdateRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Product.ProductStatus;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "*")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    // GET /api/products
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            @RequestParam MultipartFile image
    ) {
        productService.createProduct(
                name, sku, price, cost, stock,
                description, longDescription,
                status, categoryId, image
        );

        return ResponseEntity.ok("Product created successfully");
    }

    // UPDATE product
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
    // DELETE product
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }
    // GET /api/products/search?keyword=sofa
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // GET /api/products/featured
    @GetMapping("/featured")
    public List<ProductResponse> getFeaturedProducts() {
        return productService.getFeaturedProducts();
    }

    // GET /api/products/{id}/related
    @GetMapping("/{id}/related")
    public List<ProductResponse> getRelatedProducts(@PathVariable Integer id) {
        return productService.getRelatedProducts(id);
    }
    @PatchMapping("/{id}/status")
    public Product updateStatus(@PathVariable Integer id,
                                @RequestBody Map<String, String> body) {
        return productService.updateStatus(id, body.get("status"));
    }
}