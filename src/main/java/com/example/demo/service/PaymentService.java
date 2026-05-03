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

    public Payment initiatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(Integer paymentId) {
        return paymentRepository.findById(paymentId).map(p -> {
            p.setStatus(Payment.PaymentStatus.completed);
            return paymentRepository.save(p);
        }).orElse(null);
    }

    public Optional<Payment> getPaymentByOrderId(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public void processWebhook(String payload) {
        // Handle webhook logic
    }

    public List<PaymentMethod> getPaymentMethods(Integer userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    public PaymentMethod addPaymentMethod(PaymentMethod method) {
        return paymentMethodRepository.save(method);
    }

    public void deletePaymentMethod(Integer id) {
        paymentMethodRepository.deleteById(id);
    }

    public PaymentMethod setDefaultMethod(Integer id) {
        return paymentMethodRepository.findById(id).map(m -> {
            // Unset other defaults for this user
            List<PaymentMethod> others = paymentMethodRepository.findByUserId(m.getUserId());
            others.forEach(o -> o.setIsDefault(false));
            paymentMethodRepository.saveAll(others);
            
            m.setIsDefault(true);
            return paymentMethodRepository.save(m);
        }).orElse(null);
    }
}
