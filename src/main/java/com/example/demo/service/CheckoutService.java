package com.example.demo.service;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.CheckoutResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final PaymentRepository paymentRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    private final BillingAddressRepository billingAddressRepository;

    // =====================================================
    // CHECKOUT
    // =====================================================

    @Transactional
    public CheckoutResponse checkout(
            String email,
            CheckoutRequest request
    ) {

        // =================================================
        // GET USER
        // =================================================

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // =================================================
        // GET CART
        // =================================================

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        if (cartItemRepository.findByCart(cart).isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // =================================================
        // CREATE SHIPPING ADDRESS
        // =================================================

        ShippingAddress shippingAddress =
                ShippingAddress.builder()
                        .user(user)
                        .fullName(request.getFullName())
                        .phoneNumber(request.getPhoneNumber())
                        .email(request.getEmail())
                        .streetAddress(request.getStreetAddress())
                        .apartmentSuite(request.getApartmentSuite())
                        .city(request.getCity())
                        .stateProvince(request.getStateProvince())
                        .postalCode(request.getPostalCode())
                        .country(request.getCountry())
                        .build();

        shippingAddressRepository.save(shippingAddress);

        // =================================================
        // CREATE BILLING ADDRESS
        // =================================================

        BillingAddress billingAddress =
                BillingAddress.builder()
                        .user(user)
                        .fullName(request.getFullName())
                        .phoneNumber(request.getPhoneNumber())
                        .email(request.getEmail())
                        .streetAddress(request.getStreetAddress())
                        .apartmentSuite(request.getApartmentSuite())
                        .city(request.getCity())
                        .stateProvince(request.getStateProvince())
                        .postalCode(request.getPostalCode())
                        .country(request.getCountry())
                        .build();

        billingAddressRepository.save(billingAddress);

        // =================================================
        // CALCULATE TOTALS
        // =================================================

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItemRepository.findByCart(cart)) {

            BigDecimal itemTotal =
                    item.getAddedPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal shippingCost = BigDecimal.valueOf(500);

        BigDecimal totalAmount = subtotal.add(shippingCost);

        // =================================================
        // CREATE ORDER
        // =================================================

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .customerNotes(request.getCustomerNotes())
                .status(Order.OrderStatus.pending)
                .paymentStatus(Order.PaymentStatus.pending)
                .build();

        orderRepository.save(order);

        // =================================================
        // CREATE ORDER ITEMS
        // =================================================

        for (CartItem item : cartItemRepository.findByCart(cart)) {

            BigDecimal itemSubtotal =
                    item.getAddedPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getAddedPrice())
                    .subtotal(itemSubtotal)
                    .total(itemSubtotal)
                    .build();

            orderItemRepository.save(orderItem);
        }

        // =================================================
        // CREATE PAYMENT
        // =================================================

        Payment payment = Payment.builder()
                .order(order)
                .user(user)
                .amount(totalAmount)
                .gateway(request.getPaymentMethod())
                .status(Payment.PaymentStatus.pending)
                .build();

        paymentRepository.save(payment);

        // =================================================
        // CLEAR CART
        // =================================================

        cartItemRepository.deleteAll(
                cartItemRepository.findByCart(cart)
        );

        // =================================================
        // RESPONSE
        // =================================================

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getStatus().name())
                .message("Checkout completed successfully")
                .build();
    }
}