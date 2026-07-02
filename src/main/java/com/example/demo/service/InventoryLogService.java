package com.example.demo.service;

import com.example.demo.dto.response.InventorySummery;
import com.example.demo.entity.InventoryLog;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryLogRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLogService {

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public InventorySummery getInventorySummary() {

        return InventorySummery.builder()
                .totalProducts(productRepository.countAllProducts())
                .inStock(productRepository.countInStock())
                .lowStock(productRepository.countLowStock())
                .build();
    }
    // ✅ CREATE LOG (used in OrderService)
    public void createLog(
            Integer productId,
            Integer quantityChange,
            String reason,
            Integer orderId,
            String notes
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 🔥 update stock
        product.setStock(product.getStock() + quantityChange);
        productRepository.save(product);

        InventoryLog log = new InventoryLog();
        log.setProduct(product);
        log.setQuantityChange(quantityChange);
        log.setReason(reason);
        log.setNotes(notes);

        // 🔗 link order (if exists)
        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            log.setOrder(order);
        }

        inventoryLogRepository.save(log);
    }

    // ✅ GET ALL LOGS
    public List<InventoryLog> getAllLogs() {
        return inventoryLogRepository.findAll();
    }

    // ✅ GET BY PRODUCT
    public List<InventoryLog> getLogsByProduct(Integer productId) {
        return inventoryLogRepository.findByProductId(productId);
    }
}