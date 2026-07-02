package com.example.demo.controller;

import com.example.demo.dto.response.InventorySummery;
import com.example.demo.entity.InventoryLog;
import com.example.demo.service.InventoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-logs")
public class InventoryLogController {

    @Autowired
    private InventoryLogService inventoryLogService;

    @GetMapping("/summary")
    public InventorySummery getSummary() {
        return inventoryLogService.getInventorySummary();
    }
    @GetMapping
    public List<InventoryLog> getAllLogs() {
        return inventoryLogService.getAllLogs();
    }


    @GetMapping("/product/{productId}")
    public List<InventoryLog> getLogsByProduct(@PathVariable Integer productId) {
        return inventoryLogService.getLogsByProduct(productId);
    }
}