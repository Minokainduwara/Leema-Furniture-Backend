package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.enums.ProductStatus;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElseThrow();
    }

    // CREATE product
    public Product createProduct(Product product) {

        // ensure category exists
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    // UPDATE product
    public Product updateProduct(Integer id, Product updatedProduct) {

        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCost(updatedProduct.getCost());
        existing.setStock(updatedProduct.getStock());
        existing.setSku(updatedProduct.getSku());
        existing.setDescription(updatedProduct.getDescription());
        existing.setLongDescription(updatedProduct.getLongDescription());
        existing.setImage(updatedProduct.getImage());
        existing.setImages(updatedProduct.getImages());
        existing.setRating(updatedProduct.getRating());
        existing.setTotalSales(updatedProduct.getTotalSales());
        existing.setStatus(updatedProduct.getStatus());

        if (updatedProduct.getCategory() != null) {
            Category category = categoryRepository.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(category);
        }

        return productRepository.save(existing);
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