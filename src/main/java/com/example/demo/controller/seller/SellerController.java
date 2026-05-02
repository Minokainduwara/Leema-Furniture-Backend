package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.enums.RepairStatus;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private InventoryLogService inventoryLogService;

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/{userId}/dashboard")
    public Map<String, Object> dashboard(@PathVariable Integer userId) {

        Map<String, Object> data = new HashMap<>();

        data.put("orders", orderService.getSellerOrders(userId));
        data.put("repairs", repairService.getRepairsBySeller(userId));
        data.put("notifications", notificationService.getUserNotifications(userId));
        //data.put("lowStockProducts", productService.getLowStockProducts());

        return data;
    }


    @GetMapping("/{userId}/orders")
    public List<Order> getOrders(@PathVariable Integer userId) {
        return orderService.getSellerOrders(userId);
    }

    @PatchMapping("/orders/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Integer orderId,
                                   @RequestParam String status) {
        return orderService.updateStatus(orderId, status);
    }


    @GetMapping("/{userId}/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @PatchMapping("/products/{productId}/status")
    public Product updateProductStatus(@PathVariable Integer productId,
                                       @RequestParam String status) {
        return productService.updateStatus(productId, status);
    }


    @GetMapping("/{userId}/repairs")
    public List<Repair> getRepairs(@PathVariable Integer userId) {
        return repairService.getRepairsBySeller(userId);
    }

    @PatchMapping("/repairs/{repairId}/status")
    public Repair updateRepairStatus(@PathVariable Integer repairId,
                                     @RequestParam RepairStatus status) {
        return repairService.updateStatus(repairId, status);
    }


    @GetMapping("/inventory/product/{productId}")
    public List<InventoryLog> getProductLogs(@PathVariable Integer productId) {
        return inventoryLogService.getLogsByProduct(productId);
    }

    @GetMapping("/inventory/order/{orderId}")
    public List<InventoryLog> getOrderLogs(@PathVariable Integer orderId) {
        return inventoryLogService.getLogsByOrder(orderId);
    }


    @GetMapping("/{userId}/notifications")
    public List<Notification> getNotifications(@PathVariable Integer userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PatchMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }
}