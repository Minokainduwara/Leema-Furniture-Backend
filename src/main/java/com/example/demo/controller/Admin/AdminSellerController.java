package com.example.demo.controller.Admin;

import com.example.demo.dto.request.AdminRejectSellerRequest;
import com.example.demo.dto.response.AdminSellerDetailResponse;
import com.example.demo.dto.response.AdminSellerResponse;
import com.example.demo.service.AdminSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/sellers")
@RequiredArgsConstructor
public class AdminSellerController {

    private final AdminSellerService adminSellerService;

    @GetMapping
    public ResponseEntity<Page<AdminSellerResponse>> getAllSellers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(adminSellerService.getAllSellers(status, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminSellerDetailResponse> getSellerById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminSellerService.getSellerById(id));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<AdminSellerResponse> approveSeller(@PathVariable Integer id) {
        return ResponseEntity.ok(adminSellerService.approveSeller(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<AdminSellerResponse> rejectSeller(
            @PathVariable Integer id,
            @RequestBody AdminRejectSellerRequest request) {
        return ResponseEntity.ok(adminSellerService.rejectSeller(id, request.getReason()));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<AdminSellerResponse> suspendSeller(
            @PathVariable Integer id,
            @RequestParam String reason) {
        return ResponseEntity.ok(adminSellerService.suspendSeller(id, reason));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<AdminSellerResponse> activateSeller(@PathVariable Integer id) {
        return ResponseEntity.ok(adminSellerService.activateSeller(id));
    }

    @PatchMapping("/{id}/commission")
    public ResponseEntity<AdminSellerResponse> updateCommissionRate(
            @PathVariable Integer id,
            @RequestParam java.math.BigDecimal commissionRate) {
        return ResponseEntity.ok(adminSellerService.updateCommissionRate(id, commissionRate));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getSellerAnalytics() {
        return ResponseEntity.ok(adminSellerService.getSellerAnalytics());
    }
}
