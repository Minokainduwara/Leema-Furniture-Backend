package com.example.demo.service;

import com.example.demo.entity.Refund;
import com.example.demo.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;

    public Refund initiateRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    public Optional<Refund> getRefundById(Integer id) {
        return refundRepository.findById(id);
    }

    public Refund processRefund(Integer id) {
        return refundRepository.findById(id).map(r -> {
            r.setStatus(Refund.RefundStatus.completed);
            return refundRepository.save(r);
        }).orElse(null);
    }
}
