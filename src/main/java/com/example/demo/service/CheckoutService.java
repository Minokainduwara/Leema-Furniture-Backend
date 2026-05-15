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
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

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

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // =================================================
        // GET ADDRESSES
        // =================================================

        ShippingAddress shippingAddress =
                shippingAddressRepository.findById(
                        request.getShippingAddressId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Shipping address not found"
                        ));

        BillingAddress billingAddress =
                billingAddressRepository.findById(
                        request.getBillingAddressId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Billing address not found"
                        ));

        // =================================================
        // CALCULATE TOTALS
        // =================================================

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalWeightKg = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            Product product = item.getProduct();

            // =================================================
            // DEBUG LOGS
            // =================================================

            System.out.println(
                    "PRODUCT = " + product.getName()
            );

            System.out.println(
                    "WEIGHT = " + product.getWeightKg()
            );

            System.out.println(
                    "QTY = " + item.getQuantity()
            );

            // =================================================
            // CHECK STOCK
            // =================================================

            if (item.getQuantity() > product.getStock()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            // =================================================
            // ITEM TOTAL
            // =================================================

            BigDecimal itemTotal =
                    item.getAddedPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            subtotal = subtotal.add(itemTotal);

            // =================================================
            // ITEM WEIGHT
            // =================================================

            BigDecimal productWeightKg =
                    product.getWeightKg();

            if (productWeightKg == null) {
                productWeightKg = BigDecimal.ZERO;
            }

            BigDecimal itemWeight =
                    productWeightKg.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );

            totalWeightKg =
                    totalWeightKg.add(itemWeight);
        }

        // =================================================
        // DEBUG TOTAL WEIGHT
        // =================================================

        System.out.println(
                "TOTAL WEIGHT = " + totalWeightKg
        );

        // =================================================
        // SHIPPING COST
        // =================================================

        BigDecimal shippingCost =
                calculateShippingCost(totalWeightKg);

        System.out.println(
                "SHIPPING COST = " + shippingCost
        );

        // =================================================
        // FINAL TOTAL
        // =================================================

        BigDecimal totalAmount =
                subtotal.add(shippingCost);

        System.out.println(
                "SUBTOTAL = " + subtotal
        );

        System.out.println(
                "FINAL TOTAL = " + totalAmount
        );

        // =================================================
        // CREATE ORDER
        // =================================================

        Order.OrderStatus orderStatus =
                request.getPaymentMethod().equals("COD")
                        ? Order.OrderStatus.confirmed
                        : Order.OrderStatus.pending;

        Order.PaymentStatus paymentStatus =
                Order.PaymentStatus.pending;

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .customerNotes(request.getCustomerNotes())
                .paymentMethod(
                        Order.PaymentMethod.valueOf(
                                request.getPaymentMethod()
                        )
                )
                .status(orderStatus)
                .paymentStatus(paymentStatus)
                .build();

        orderRepository.save(order);

        // =================================================
        // CREATE ORDER ITEMS
        // =================================================

        for (CartItem item : cartItems) {

            BigDecimal itemSubtotal =
                    item.getAddedPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

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
        // REDUCE STOCK
        // =================================================

        for (CartItem item : cartItems) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() - item.getQuantity()
            );

            productRepository.save(product);
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
        // CREATE INVOICE
        // =================================================

        Invoice invoice = Invoice.builder()
                .order(order)
                .payment(payment)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .build();

        invoiceRepository.save(invoice);

        // =================================================
        // SEND ORDER CONFIRMATION EMAIL
        // =================================================

        emailService.sendOrderConfirmationEmail(order);

        // =================================================
        // CLEAR CART
        // =================================================

        cartItemRepository.deleteAll(cartItems);

        // =================================================
        // RESPONSE
        // =================================================

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getStatus().name())
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .message("Checkout completed successfully")
                .build();
    }

    // =====================================================
    // SHIPPING COST CALCULATION
    // =====================================================

    private BigDecimal calculateShippingCost(
            BigDecimal totalWeightKg
    ) {

        if (totalWeightKg.compareTo(
                BigDecimal.valueOf(10)
        ) > 0) {

            return BigDecimal.valueOf(10000);

        } else if (totalWeightKg.compareTo(
                BigDecimal.valueOf(5)
        ) >= 0) {

            return BigDecimal.valueOf(5000);

        } else {

            return BigDecimal.valueOf(1000);
        }
    }
}