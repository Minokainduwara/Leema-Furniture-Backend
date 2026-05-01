package com.example.demo.service;

import com.example.demo.entity.Order;
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

        return orderRepository.save(order);
    }

    public void cancelOrder(Integer id) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public String generateInvoice(Integer id) {
        Order order = getOrderById(id);
        return "Invoice for Order ID: " + order.getId();
    }
    public Order updateStatus(Integer id, String status) {

        Order order = getOrderById(id);

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());

        order.setStatus(orderStatus);

        return orderRepository.save(order);
    }
}