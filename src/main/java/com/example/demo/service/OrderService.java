package com.example.demo.service;

import com.example.demo.dto.response.OrderHistoryResponse;
import com.example.demo.dto.response.OrderItemResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderHistory;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.OrderHistoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    private OrderResponse map(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())

                // ✅ ORDER DETAILS
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO)
                .shippingCost(order.getShippingCost() != null ? order.getShippingCost() :BigDecimal.ZERO)
                .paymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "PENDING")

                // ✅ USER
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getCustomerName())
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)

                // ✅ ITEMS
                .items(
                        order.getOrderItems().stream().map(item ->
                                OrderItemResponse.builder()
                                        .id(item.getId())
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .productCode(item.getProduct().getSku())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .subtotal(item.getSubtotal())

                                        // ✅ ADD IMAGE (IMPORTANT FOR UI)
                                        .imageUrl(item.getProduct().getImage())

                                        .build()
                        ).toList()
                )
                .build();
    }

    // ================= GET ALL ORDERS =================
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    // ================= GET USER ORDERS =================
    public List<Order> getUserOrders(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserId(userId, pageable).getContent();
    }

    // ================= GET ORDER BY ID =================
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ================= CREATE ORDER =================
    public Order createOrder(Order order) {

        if (order.getUser() == null) {
            throw new RuntimeException("User is required");
        }

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);

        Order saved = orderRepository.save(order);

        saveHistory(saved, "PENDING", "Order created", user);

        return saved;
    }

    // ================= CANCEL ORDER =================
    public void cancelOrder(Integer id) {

        Order order = getOrderById(id);

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        saveHistory(saved, "CANCELLED", "Order cancelled", saved.getUser());
    }

    // ================= UPDATE STATUS =================
    public Order updateStatus(Integer id, String status) {

        Order order = getOrderById(id);

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));

        Order saved = orderRepository.save(order);

        saveHistory(
                saved,
                status.toUpperCase(),
                "Status changed from " + oldStatus + " to " + status,
                saved.getUser()
        );
        Notification notification = Notification.builder()
                .user(saved.getUser())
                .type("ORDER")
                .title("Order Status Updated")
                .message("Your order " + saved.getOrderNumber() + " is now " + status)
                .isRead(false)
                .orderNumber(saved.getOrderNumber())
                .build();

        notificationRepository.save(notification);

        return saved;
    }

    // ================= SELLER ORDERS =================
    public List<Order> getSellerOrders(Integer userId) {
        return orderRepository.findByUser_Id(userId);
    }

    // ================= SEARCH =================
    public List<Order> searchOrders(String query) {
        return orderRepository.findByOrderNumberContainingIgnoreCase(query);
    }
    public List<OrderResponse> getPendingOrders() {
        return orderRepository
                .findByStatusOrderByCreatedAtDesc(Order.OrderStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }
    public List<Map<String, Object>> getRevenuePerDay() {
        return orderRepository.getRevenuePerDay();
    }
    public long getPendingOrderCount() {
        return orderRepository.countByStatus(Order.OrderStatus.PENDING);
    }
    public List<Map<String, Object>> getOrdersPerDay() {
        return orderRepository.getOrdersPerDay();
    }
    // ================= STATUS FILTER =================
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // ================= DATE FILTER =================
    public List<Order> filterByDate(String type) {

        LocalDateTime now = LocalDateTime.now();

        return switch (type.toLowerCase()) {
            case "today" ->
                    orderRepository.findByCreatedAtAfter(now.toLocalDate().atStartOfDay());

            case "week" ->
                    orderRepository.findByCreatedAtAfter(now.minusDays(7));

            case "month" ->
                    orderRepository.findByCreatedAtAfter(now.minusMonths(1));

            default -> orderRepository.findAll();
        };
    }

    // ================= ORDER STATUS =================
    public Order updateOrderStatus(Integer id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));

        return orderRepository.save(order);
    }

    // ================= PAYMENT STATUS =================
    public Order updatePaymentStatus(Integer id, String paymentStatus) {

        Order order = getOrderById(id);

        order.setPaymentStatus(Order.PaymentStatus.valueOf(paymentStatus.toUpperCase()));
        Order saved = orderRepository.save(order);

        saveHistory(saved, paymentStatus, "Payment updated", saved.getUser());

        return saved;
    }

    // ================= RECENT ORDERS =================
    public List<OrderResponse> getRecentOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(o -> OrderResponse.builder()
                        .id(o.getId())
                        .orderNumber(o.getOrderNumber())
                        .status(o.getStatus().name())
                        .totalAmount(o.getTotalAmount())
                        .build()
                )
                .toList();
    }

    // ================= ORDER HISTORY BY ORDER ID =================
    public List<OrderHistoryResponse> getOrderHistoryByOrderId(Integer orderId) {

        return orderHistoryRepository
                .findByOrder_IdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(h -> OrderHistoryResponse.builder()
                        .id(h.getId())
                        .status(h.getStatus())
                        .message(h.getMessage())
                        .changedBy(h.getChangedBy() != null
                                ? h.getChangedBy().getName()
                                : "SYSTEM")
                        .createdAt(h.getCreatedAt())
                        .build()
                )
                .toList();
    }

    // ================= USER HISTORY =================
    public List<OrderHistory> getOrderHistoryByUser(Integer userId) {
        return orderHistoryRepository
                .findByOrder_User_IdOrderByCreatedAtDesc(userId);
    }

    // ================= SAVE HISTORY (IMPORTANT FIX) =================
    private void saveHistory(Order order, String status, String message, User user) {

        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setMessage(message);
        history.setChangedBy(user);

        orderHistoryRepository.save(history);
    }
    public List<OrderResponse> getMyOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::map)
                .toList();
    }
    public List<OrderHistoryResponse> getMyOrderHistory(Integer orderId, String email) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Security check
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return orderHistoryRepository
                .findByOrder_IdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(h -> OrderHistoryResponse.builder()
                        .id(h.getId())
                        .status(h.getStatus())
                        .message(h.getMessage())
                        .createdAt(h.getCreatedAt())
                        .changedBy(h.getChangedBy() != null
                                ? h.getChangedBy().getName()
                                : "SYSTEM")
                        .build()
                )
                .toList();
    }
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }
}