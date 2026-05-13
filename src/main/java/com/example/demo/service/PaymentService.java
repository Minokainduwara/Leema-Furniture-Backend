package com.example.demo.service;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.repository.PaymentMethodRepository;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    // =========================================================
    // INITIATE PAYMENT
    // =========================================================

    public Payment initiatePayment(Payment payment) {

        if (payment == null) {
            throw new RuntimeException("Payment data is required");
        }

        if (payment.getOrder() == null) {
            throw new RuntimeException("Order is required");
        }

        if (payment.getUser() == null) {
            throw new RuntimeException("User is required");
        }

        if (payment.getAmount() == null) {
            throw new RuntimeException("Amount is required");
        }

        if (payment.getGateway() == null ||
                payment.getGateway().isBlank()) {

            payment.setGateway("manual");
        }

        if (payment.getStatus() == null) {
            payment.setStatus(Payment.PaymentStatus.pending);
        }

        return paymentRepository.save(payment);
    }

    // =========================================================
    // CONFIRM PAYMENT
    // =========================================================

    public Payment confirmPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.completed);

        return paymentRepository.save(payment);
    }

    // =========================================================
    // FAIL PAYMENT
    // =========================================================

    public Payment failPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.failed);

        return paymentRepository.save(payment);
    }

    // =========================================================
    // CANCEL PAYMENT
    // =========================================================

    public Payment cancelPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.cancelled);

        return paymentRepository.save(payment);
    }

    // =========================================================
    // REFUND PAYMENT
    // =========================================================

    public Payment refundPayment(Integer paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.refunded);

        return paymentRepository.save(payment);
    }

    // =========================================================
    // GET PAYMENT BY ORDER
    // =========================================================

    public Optional<Payment> getPaymentByOrderId(
            Integer orderId
    ) {

        return paymentRepository.findByOrderId(orderId);
    }

    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    public Payment getPaymentById(Integer paymentId) {

        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));
    }

    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }

    // =========================================================
    // PROCESS WEBHOOK
    // =========================================================

    public void processWebhook(String payload) {

        if (payload == null || payload.isBlank()) {
            throw new RuntimeException("Webhook payload is empty");
        }

        System.out.println("Webhook received:");
        System.out.println(payload);

        // Future:
        // Stripe webhook
        // PayPal webhook
        // PayHere webhook
        // KOKO webhook
    }

    // =========================================================
    // PAYMENT METHODS
    // =========================================================

    public List<PaymentMethod> getPaymentMethods(
            Integer userId
    ) {

        return paymentMethodRepository.findByUserId(userId);
    }

    // =========================================================
    // GET DEFAULT PAYMENT METHOD
    // =========================================================

    public Optional<PaymentMethod> getDefaultPaymentMethod(
            Integer userId
    ) {

        return paymentMethodRepository
                .findByUserId(userId)
                .stream()
                .filter(method ->
                        Boolean.TRUE.equals(
                                method.getIsDefault()
                        )
                )
                .findFirst();
    }

    // =========================================================
    // ADD PAYMENT METHOD
    // =========================================================

    public PaymentMethod addPaymentMethod(
            PaymentMethod method
    ) {

        if (method == null) {
            throw new RuntimeException(
                    "Payment method is required"
            );
        }

        if (method.getUser() == null) {
            throw new RuntimeException(
                    "User is required"
            );
        }

        if (method.getMethodType() == null) {
            throw new RuntimeException(
                    "Payment method type is required"
            );
        }

        // First payment method becomes default
        List<PaymentMethod> existing =
                paymentMethodRepository.findByUserId(
                        method.getUser().getId()
                );

        if (existing.isEmpty()) {
            method.setIsDefault(true);
        }

        return paymentMethodRepository.save(method);
    }

    // =========================================================
    // DELETE PAYMENT METHOD
    // =========================================================

    public void deletePaymentMethod(Integer id) {

        PaymentMethod method =
                paymentMethodRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment method not found"
                                ));

        paymentMethodRepository.delete(method);
    }

    // =========================================================
    // SET DEFAULT METHOD
    // =========================================================

    public PaymentMethod setDefaultMethod(Integer id) {

        PaymentMethod method =
                paymentMethodRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment method not found"
                                ));

        Integer userId = method.getUser().getId();

        // Remove old defaults
        List<PaymentMethod> methods =
                paymentMethodRepository.findByUserId(userId);

        for (PaymentMethod m : methods) {
            m.setIsDefault(false);
        }

        paymentMethodRepository.saveAll(methods);

        // Set new default
        method.setIsDefault(true);

        return paymentMethodRepository.save(method);
    }
}