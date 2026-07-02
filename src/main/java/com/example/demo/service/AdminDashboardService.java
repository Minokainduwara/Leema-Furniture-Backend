package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getStats() {
        List<Order> allOrders = orderRepository.findAll();
        List<Product> allProducts = productRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime monthStart = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long ordersThisMonth = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(monthStart))
                .count();

        long activeProducts = allProducts.stream()
                .filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE)
                .count();

        long totalCustomers = allUsers.stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .count();

        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (Order o : allOrders) {
            if (o.getStatus() == null) continue;
            String key = o.getStatus().name().toUpperCase();
            ordersByStatus.merge(key, 1L, Long::sum);
        }

        List<Map<String, Object>> bestSellers = allProducts.stream()
                .sorted(Comparator.comparing(
                        (Product p) -> p.getTotalSales() == null ? 0 : p.getTotalSales()
                ).reversed())
                .limit(5)
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("image", p.getImage());
                    m.put("price", p.getPrice());
                    m.put("totalSales", p.getTotalSales() == null ? 0 : p.getTotalSales());
                    return m;
                })
                .toList();

        List<Map<String, Object>> recentOrders = allOrders.stream()
                .sorted(Comparator.comparing(
                        Order::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(10)
                .map(o -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", o.getId());
                    m.put("orderNumber", o.getOrderNumber());
                    m.put("customerName", o.getUser() == null ? null : o.getUser().getName());
                    m.put("status", o.getStatus() == null ? null : o.getStatus().name());
                    m.put("paymentStatus", o.getPaymentStatus() == null ? null : o.getPaymentStatus().name());
                    m.put("totalAmount", o.getTotalAmount());
                    m.put("createdAt", o.getCreatedAt());
                    return m;
                })
                .toList();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalOrders", (long) allOrders.size());
        stats.put("ordersThisMonth", ordersThisMonth);
        stats.put("activeProducts", activeProducts);
        stats.put("totalProducts", (long) allProducts.size());
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalUsers", (long) allUsers.size());
        stats.put("ordersByStatus", ordersByStatus);
        stats.put("bestSellers", bestSellers);
        stats.put("recentOrders", recentOrders);
        return stats;
    }
}
