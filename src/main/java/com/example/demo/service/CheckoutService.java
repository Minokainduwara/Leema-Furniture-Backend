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
    public CheckoutResponse checkout(String email, CheckoutRequest request) {
        System.out.println("=== CHECKOUT STARTED ===");
        System.out.println("User email: " + email);
        System.out.println("Request: " + request);

        // ================= USER =================
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.err.println("ERROR: User not found with email: " + email);
                    return new RuntimeException("User not found: " + email);
                });

        System.out.println("User found: " + user.getName() + " (ID: " + user.getId() + ")");

        // ================= CART =================
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> {
                    System.err.println("ERROR: Cart not found for user: " + user.getName());
                    return new RuntimeException("Cart not found for user: " + user.getName());
                });

        System.out.println("Cart found: ID=" + cart.getId());

        List<CartItem> items = cartItemRepository.findByCart(cart);
        System.out.println("Cart items count: " + items.size());

        if (items.isEmpty()) {
            System.err.println("ERROR: Cart is empty");
            throw new RuntimeException("Cart is empty");
        }

        // ================= ADDRESS =================
        ShippingAddress shippingAddress = ShippingAddress.builder()
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

        BillingAddress billingAddress = BillingAddress.builder()
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

        // ================= TOTAL =================
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : items) {
            System.out.println("Processing cart item: Product=" + item.getProduct().getName() + 
                             ", Quantity=" + item.getQuantity() + 
                             ", Stock=" + item.getProduct().getStock() +
                             ", Price=" + item.getAddedPrice());

            if (item.getQuantity() > item.getProduct().getStock()) {
                System.err.println("ERROR: Insufficient stock for " + item.getProduct().getName() + 
                                  ". Requested: " + item.getQuantity() + 
                                  ", Available: " + item.getProduct().getStock());
                throw new RuntimeException("Insufficient stock: " + item.getProduct().getName() + 
                                          ". Available: " + item.getProduct().getStock() + 
                                          ", Requested: " + item.getQuantity());
            }

            BigDecimal itemTotal = item.getAddedPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(itemTotal);
        }
        
        System.out.println("Subtotal: " + subtotal);

        BigDecimal shippingCost = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal;

        // ================= SAFE ENUM HANDLING =================
        Order.PaymentMethod paymentMethod;
        try {
            paymentMethod = Order.PaymentMethod.valueOf(
                    request.getPaymentMethod().trim().toUpperCase()
            );
            System.out.println("Payment method: " + paymentMethod);
        } catch (Exception e) {
            System.err.println("ERROR: Invalid payment method: " + request.getPaymentMethod());
            throw new RuntimeException("Invalid payment method: " + request.getPaymentMethod());
        }

        Order.OrderStatus orderStatus =
                paymentMethod == Order.PaymentMethod.COD
                        ? Order.OrderStatus.CONFIRMED
                        : Order.OrderStatus.PENDING;

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .customerNotes(request.getCustomerNotes())
                .paymentMethod(paymentMethod)
                .status(orderStatus)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .build();

        orderRepository.save(order);

        // ================= ORDER ITEMS =================
        for (CartItem item : items) {
            BigDecimal itemSubtotal = item.getAddedPrice()
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

        // ================= STOCK UPDATE =================
        for (CartItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // ================= PAYMENT =================
        Payment payment = Payment.builder()
                .order(order)
                .user(user)
                .amount(totalAmount)
                .gateway(paymentMethod.name())
                .status(Payment.PaymentStatus.pending)
                .build();

        paymentRepository.save(payment);

        // ================= INVOICE =================
        Invoice invoice = Invoice.builder()
                .order(order)
                .payment(payment)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .build();

        invoiceRepository.save(invoice);

        // ================= EMAIL =================
        try {
            emailService.sendOrderConfirmationEmail(order);
            System.out.println("Confirmation email sent");
        } catch (Exception e) {
            System.err.println("WARNING: Failed to send email: " + e.getMessage());
            // Don't fail checkout if email fails
        }

        // ================= CLEAR CART =================
        cartItemRepository.deleteAll(items);
        System.out.println("Cart cleared");

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .paymentMethod(paymentMethod.name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getStatus().name())
                .message("Checkout completed successfully")
                .build();
    }
    
    // ================= HELPER METHOD FOR DEBUGGING =================
    public String debugCart(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "User not found: " + email;
        }
        
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            return "Cart not found for user: " + user.getName();
        }
        
        List<CartItem> items = cartItemRepository.findByCart(cart);
        StringBuilder sb = new StringBuilder();
        sb.append("Cart ID: ").append(cart.getId()).append("\n");
        sb.append("Items count: ").append(items.size()).append("\n");
        
        for (CartItem item : items) {
            sb.append("- ").append(item.getProduct().getName())
              .append(" | Qty: ").append(item.getQuantity())
              .append(" | Stock: ").append(item.getProduct().getStock())
              .append(" | Price: ").append(item.getAddedPrice())
              .append("\n");
        }
        
        return sb.toString();
    }
}
