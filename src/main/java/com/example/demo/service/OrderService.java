package com.example.demo.service;

import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.entity.Order.OrderStatus;
import com.example.demo.factory.NotificationFactory;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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

    @Autowired
    private NotificationRepository notificationRepository;

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
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        Order savedOrder = orderRepository.save(order);

        // 🔻 REDUCE STOCK WHEN ORDER CREATED
        for (OrderItem item : savedOrder.getOrderItems()) {

            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    -item.getQuantity(),
                    "PURCHASE",
                    savedOrder.getId(),
                    "Stock reduced when order placed"
            );
        }

        return savedOrder;
    }

    // ================= CANCEL ORDER =================
    public void cancelOrder(Integer id) {

        Order order = getOrderById(id);

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 🔺 RESTORE STOCK
        if (oldStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {

                inventoryLogService.createLog(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        "CANCELLED",
                        order.getId(),
                        "Stock restored due to cancellation"
                );
            }
        }
    }

    // ================= UPDATE STATUS =================
    public Order updateStatus(Integer id, String status) {

        Order order = getOrderById(id);

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        OrderStatus oldStatus = order.getStatus();

        // ✅ VALIDATION
        List<OrderStatus> allowed = allowedTransitions.get(oldStatus);

        if (allowed == null || !allowed.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition: " + oldStatus + " → " + newStatus
            );
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        // 🔥 INVENTORY UPDATE
        handleInventoryUpdate(oldStatus, newStatus, savedOrder);

        // 🔔 NOTIFICATION
        if (!oldStatus.equals(newStatus)) {

            Notification notification =
                    NotificationFactory.createOrderNotification(
                            order.getUser(),
                            newStatus.name(),
                            order.getOrderNumber()
                    );

            notificationRepository.save(notification);
        }

        return savedOrder;
    }

    // ================= INVENTORY LOGIC =================
    private void handleInventoryUpdate(OrderStatus oldStatus,
                                       OrderStatus newStatus,
                                       Order order) {

        System.out.println("🔥 Inventory Update: " + oldStatus + " → " + newStatus);

        for (OrderItem item : order.getOrderItems()) {

            Integer productId = item.getProduct().getId();
            Integer qty = item.getQuantity();

            // 🔺 CANCELLED → RESTORE STOCK
            if (newStatus == OrderStatus.CANCELLED &&
                    oldStatus != OrderStatus.CANCELLED) {

                inventoryLogService.createLog(
                        productId,
                        qty,
                        "CANCELLED",
                        order.getId(),
                        "Stock restored due to cancellation"
                );
            }

            // 🔺 RETURNED → RESTORE STOCK
            else if (newStatus == OrderStatus.RETURNED &&
                    oldStatus != OrderStatus.RETURNED) {

                inventoryLogService.createLog(
                        productId,
                        qty,
                        "RETURNED",
                        order.getId(),
                        "Stock restored after return"
                );
            }
        }
    }

    // ================= SELLER ORDERS =================
    public List<Order> getSellerOrders(Integer userId) {
        return orderRepository.findByHandledById(userId);
    }

    // ================= SEARCH =================
    public List<Order> searchOrders(String query) {
        return orderRepository
                .findByOrderNumberContainingIgnoreCaseOrUser_NameContainingIgnoreCase(
                        query,
                        query
                );
    }

    // ================= STATUS FILTER =================
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // ================= DATE FILTER =================
    public List<Order> filterByDate(String type) {

        LocalDateTime now = LocalDateTime.now();

        if (type.equalsIgnoreCase("TODAY")) {
            return orderRepository.findByCreatedAtGreaterThanEqual(
                    now.toLocalDate().atStartOfDay()
            );
        }

        if (type.equalsIgnoreCase("WEEK")) {
            return orderRepository.findByCreatedAtGreaterThanEqual(
                    now.minusDays(7)
            );
        }

        if (type.equalsIgnoreCase("MONTH")) {
            return orderRepository.findByCreatedAtGreaterThanEqual(
                    now.minusMonths(1)
            );
        }

        return List.of();
    }

    // ================= PAYMENT STATUS =================
    public Order updatePaymentStatus(Integer id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(Order.PaymentStatus.valueOf(status.toUpperCase()));

        return orderRepository.save(order);
    }
    public List<OrderResponse> getRecentOrders(String email) {

        List<Order> orders =
                orderRepository.findTop5ByUser_EmailOrderByCreatedAtDesc(email);

        return orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getStatus().name(),
                        order.getTotalAmount()
                ))
                .toList();
    }
}