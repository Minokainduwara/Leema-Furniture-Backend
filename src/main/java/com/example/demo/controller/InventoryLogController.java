package com.example.demo.controller;

import com.example.demo.entity.InventoryLog;
import com.example.demo.service.InventoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryLogController {

    @Autowired
    private InventoryLogService inventoryLogService;

    // GET logs by product
    @GetMapping("/product/{productId}")
    public List<InventoryLog> getByProduct(@PathVariable Integer productId) {
        return inventoryLogService.getLogsByProduct(productId);
    }

    // GET logs by order
    @GetMapping("/order/{orderId}")
    public List<InventoryLog> getByOrder(@PathVariable Integer orderId) {
        return inventoryLogService.getLogsByOrder(orderId);
    }
}