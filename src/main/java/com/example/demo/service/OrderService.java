package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryLogService inventoryLogService;

    // ================= STATUS RULES =================
    private static final Map<OrderStatus, List<OrderStatus>> allowedTransitions = Map.of(
            OrderStatus.PENDING, List.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, List.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, List.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, List.of(OrderStatus.RETURNED)
    );

    // ================= GET ALL =================
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ================= GET USER ORDERS =================
    public List<Order> getUserOrders(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserId(userId, pageable).getContent();
    }

    // ================= GET BY ID =================
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ================= CREATE ORDER =================
    public Order createOrder(Order order) {

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);

        // link items to order
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        Order savedOrder = orderRepository.save(order);

        // inventory deduction
        for (OrderItem item : savedOrder.getItems()) {
            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    -item.getQuantity(),
                    "PURCHASE",
                    savedOrder.getId(),
                    "Order placed"
            );
        }

        return savedOrder;
    }

    // ================= CANCEL ORDER =================
    public void cancelOrder(Integer id) {

        Order order = getOrderById(id);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // restore stock
        for (OrderItem item : order.getItems()) {
            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    "RETURN",
                    order.getId(),
                    "Order cancelled"
            );
        }
    }

    // ================= INVOICE =================
    public String generateInvoice(Integer id) {
        Order order = getOrderById(id);
        return "Invoice for Order ID: " + order.getId();
    }

    // ================= UPDATE STATUS (SAFE) =================
    public Order updateStatus(Integer id, String status) {

        Order order = getOrderById(id);

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderStatus currentStatus = order.getStatus();

        List<OrderStatus> allowed = allowedTransitions.get(currentStatus);

        if (allowed == null || !allowed.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition: " + currentStatus + " → " + newStatus
            );
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    // ================= SELLER ORDERS =================
    public List<Order> getSellerOrders(Integer userId) {
        return orderRepository.findByHandledById(userId);
    }
}