package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.CheckoutResponse;
import com.example.demo.service.CheckoutService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService checkoutService;

    // =====================================================
    // CHECKOUT
    // =====================================================

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        CheckoutResponse response =
                checkoutService.checkout(email, request);

        return ResponseEntity.ok(response);
    }
}