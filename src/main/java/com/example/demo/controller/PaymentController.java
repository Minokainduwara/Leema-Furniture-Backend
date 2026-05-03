package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // Payments
    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiatePayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.initiatePayment(payment));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Payment> confirmPayment(@RequestParam Integer paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Integer orderId) {
        return paymentService.getPaymentByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) {
        paymentService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }

    // Payment Methods
    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethods(@RequestParam Integer userId) {
        return ResponseEntity.ok(paymentService.getPaymentMethods(userId));
    }

    @PostMapping("/methods")
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod method) {
        return ResponseEntity.ok(paymentService.addPaymentMethod(method));
    }

    @DeleteMapping("/methods/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Integer id) {
        paymentService.deletePaymentMethod(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/methods/{id}/default")
    public ResponseEntity<PaymentMethod> setDefaultMethod(@PathVariable Integer id) {
        PaymentMethod method = paymentService.setDefaultMethod(id);
        return method != null ? ResponseEntity.ok(method) : ResponseEntity.notFound().build();
    }
}
