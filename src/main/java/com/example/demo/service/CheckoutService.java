package com.example.demo.service;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.CheckoutResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final PaymentRepository paymentRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    private final BillingAddressRepository billingAddressRepository;

    private final InventoryLogService inventoryLogService;

    // =========================================================
    // CHECKOUT
    // =========================================================

    @Transactional
    public CheckoutResponse checkout(
            String email,
            CheckoutRequest request
    ) {

        // =====================================================
        // USER
        // =====================================================

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // =====================================================
        // CART
        // =====================================================

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // =====================================================
        // STOCK VALIDATION
        // =====================================================

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(
                    item.getProductId()
            ).orElseThrow(() ->
                    new RuntimeException("Product not found"));

            if (product.getStock() < item.getQuantity()) {

                throw new RuntimeException(
                        product.getName() +
                                " does not have enough stock"
                );
            }
        }

        // =====================================================
        // SHIPPING ADDRESS
        // =====================================================

        ShippingAddress shippingAddress =
                ShippingAddress.builder()
                        .userId(user.getId())
                        .fullName(request.getFullName())
                        .phoneNumber(request.getPhoneNumber())
                        .email(request.getEmail())
                        .streetAddress(request.getStreetAddress())
                        .apartmentSuite(request.getApartmentSuite())
                        .city(request.getCity())
                        .stateProvince(request.getStateProvince())
                        .postalCode(request.getPostalCode())
                        .country(request.getCountry())
                        .isDefault(false)
                        .build();

        shippingAddressRepository.save(shippingAddress);

        // =====================================================
        // BILLING ADDRESS
        // =====================================================

        BillingAddress billingAddress =
                BillingAddress.builder()
                        .userId(user.getId())
                        .fullName(request.getFullName())
                        .phoneNumber(request.getPhoneNumber())
                        .email(request.getEmail())
                        .streetAddress(request.getStreetAddress())
                        .apartmentSuite(request.getApartmentSuite())
                        .city(request.getCity())
                        .stateProvince(request.getStateProvince())
                        .postalCode(request.getPostalCode())
                        .country(request.getCountry())
                        .isDefault(false)
                        .build();

        billingAddressRepository.save(billingAddress);

        // =====================================================
        // TOTALS
        // =====================================================

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            BigDecimal itemTotal =
                    item.getAddedPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal shippingCost =
                BigDecimal.valueOf(971);

        BigDecimal totalAmount =
                subtotal.add(shippingCost);

        // =====================================================
        // CREATE ORDER
        // =====================================================

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .orderNumber(
                        "ORD-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                                        .toUpperCase()
                )
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .customerNotes(request.getCustomerNotes())
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .build();

        orderRepository.save(order);

        // =====================================================
        // ORDER ITEMS
        // =====================================================

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(
                    item.getProductId()
            ).orElseThrow(() ->
                    new RuntimeException("Product not found"));

            BigDecimal itemSubtotal =
                    item.getAddedPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(item.getQuantity())
                            .unitPrice(item.getAddedPrice())
                            .subtotal(itemSubtotal)
                            .tax(BigDecimal.ZERO)
                            .total(itemSubtotal)
                            .build();

            orderItemRepository.save(orderItem);

            // =================================================
            // REDUCE STOCK
            // =================================================

            inventoryLogService.createLog(
                    product.getId(),
                    -item.getQuantity(),
                    "purchase",
                    order.getId(),
                    "Stock reduced after checkout"
            );
        }

        // =====================================================
        // PAYMENT
        // =====================================================

        Payment payment = Payment.builder()
                .order(order)
                .user(user)
                .amount(totalAmount)
                .gateway(request.getPaymentMethod())
                .status(Payment.PaymentStatus.pending)
                .build();

        paymentRepository.save(payment);

        // =====================================================
        // CLEAR CART
        // =====================================================

        cartItemRepository.deleteAll(cartItems);

        // =====================================================
        // RESPONSE
        // =====================================================

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(payment.getStatus().name())
                .orderStatus(order.getStatus().name())
                .message("Order placed successfully")
                .build();
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Transactional
    public void cancelOrder(Integer orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() ==
                Order.OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order already cancelled"
            );
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        // =====================================================
        // RESTORE STOCK
        // =====================================================

        for (OrderItem item : orderItems) {

            inventoryLogService.createLog(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    "return",
                    order.getId(),
                    "Stock restored after cancellation"
            );
        }

        // =====================================================
        // UPDATE ORDER
        // =====================================================

        order.setStatus(Order.OrderStatus.CANCELLED);

        order.setPaymentStatus(
                Order.PaymentStatus.CANCELLED
        );

        orderRepository.save(order);

        // =====================================================
        // UPDATE PAYMENT
        // =====================================================

        Payment payment =
                paymentRepository
                        .findByOrderId(orderId)
                        .orElse(null);

        if (payment != null) {

            payment.setStatus(
                    Payment.PaymentStatus.cancelled
            );

            paymentRepository.save(payment);
        }
    }
}