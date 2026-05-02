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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InventoryLogService inventoryLogService;

    public List<Order> getUserOrders(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return orderRepository.findByUserId(userId, pageable).getContent();
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public Order createOrder(Order order) {

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);

        // 1. SAVE ORDER FIRST
        Order savedOrder = orderRepository.save(order);

        // 2. LOOP ORDER ITEMS (IMPORTANT)
        for (OrderItem item : savedOrder.getOrderItems()) {

            // reduce stock logic happens elsewhere (ProductService ideally)

            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    -item.getQuantity(),   // STOCK REDUCTION
                    "PURCHASE",
                    savedOrder.getId(),
                    "Order placed"
            );
        }

        return savedOrder;
    }

    public void cancelOrder(Integer id) {

        Order order = getOrderById(id);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // restore inventory
        for (OrderItem item : order.getOrderItems()) {

            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    item.getQuantity(),   // STOCK BACK
                    "RETURN",
                    order.getId(),
                    "Order cancelled"
            );
        }
    }

    public String generateInvoice(Integer id) {
        Order order = getOrderById(id);
        return "Invoice for Order ID: " + order.getId();
    }
    public Order updateStatus(Integer id, String status) {

        Order order = getOrderById(id);
        //User currentUser = getCurrentUser();
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

        order.setStatus(orderStatus);
        //order.setHandledBy(currentUser);
        return orderRepository.save(order);
    }
    public List<Order> getSellerOrders(Integer userId) {
        return orderRepository.findByHandledById(userId);
    }
}