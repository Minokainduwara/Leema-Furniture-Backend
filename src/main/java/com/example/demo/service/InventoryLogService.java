package com.example.demo.service;

import com.example.demo.entity.InventoryLog;
import com.example.demo.repository.InventoryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryLogService {

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    public void createLog(Integer productId,
                          Integer quantityChange,
                          String reason,
                          Integer orderId,
                          String notes) {

        InventoryLog log = new InventoryLog();
        log.setProductId(productId);
        log.setQuantityChange(quantityChange);
        log.setReason(reason);
        log.setOrderId(orderId);
        log.setNotes(notes);

        inventoryLogRepository.save(log);
    }

    public List<InventoryLog> getLogsByProduct(Integer productId) {
        return inventoryLogRepository.findByProductId(productId);
    }

    public List<InventoryLog> getLogsByOrder(Integer orderId) {
        return inventoryLogRepository.findByOrderId(orderId);
    }
}