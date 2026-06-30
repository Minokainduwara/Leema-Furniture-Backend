package com.example.demo.controller;

import com.example.demo.dto.response.OrderHistoryResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderHistory;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/all")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping
    public List<Order> getUserOrders(@RequestParam Integer userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return orderService.getUserOrders(userId, page, size);
    }


    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }


    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }


    @PatchMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return "Order cancelled successfully";
    }


    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Integer id,
                              @RequestBody Map<String, String> body) {
        return orderService.updateStatus(id, body.get("status"));
    }


    @GetMapping("/{userId}/orders")
    public List<Order> getSellerOrders(@PathVariable Integer userId) {
        return orderService.getSellerOrders(userId);
    }


    @GetMapping("/search")
    public List<Order> searchOrders(@RequestParam String query) {
        return orderService.searchOrders(query);
    }


    @GetMapping("/status")
    public List<Order> getByStatus(@RequestParam Order.OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }


    @GetMapping("/filter")
    public List<Order> filterByDate(@RequestParam String type) {
        return orderService.filterByDate(type);
    }


    @PatchMapping("/{id}/payment-status")
    public Order updatePaymentStatus(@PathVariable Integer id,
                                     @RequestBody Map<String, String> body) {
        return orderService.updatePaymentStatus(id, body.get("paymentStatus"));
    }


    @GetMapping("/recent")
    public List<OrderResponse> getRecentOrders(Authentication auth) {
        return orderService.getRecentOrders(auth.getName());
    }

    @GetMapping("/my-orders")
    public List<OrderResponse> getMyOrders(Authentication auth) {
        return orderService.getMyOrders(auth.getName());
    }

    @GetMapping("/{orderId}/history")
    public List<OrderHistoryResponse> getOrderHistory(@PathVariable Integer orderId) {
        return orderService.getOrderHistoryByOrderId(orderId);
    }


    @GetMapping("/user/{userId}/history")
    public List<OrderHistory> getUserOrderHistory(@PathVariable Integer userId) {
        return orderService.getOrderHistoryByUser(userId);
    }
}