package com.example.demo.controller;

import com.example.demo.entity.Refund;
import com.example.demo.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<Refund> initiateRefund(@RequestBody Refund refund) {
        return ResponseEntity.ok(refundService.initiateRefund(refund));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Refund> getRefundById(@PathVariable Integer id) {
        return refundService.getRefundById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/admin/{id}/process")
    public ResponseEntity<Refund> processRefund(@PathVariable Integer id) {
        Refund processed = refundService.processRefund(id);
        return processed != null ? ResponseEntity.ok(processed) : ResponseEntity.notFound().build();
    }
}
