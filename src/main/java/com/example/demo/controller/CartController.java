package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.RemoveCartItemRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(auth.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> add(Authentication auth,
                                            @Valid @RequestBody AddToCartRequest req) {
        return ResponseEntity.ok(cartService.addToCart(auth.getName(), req));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(Authentication auth,
                                               @Valid @RequestBody UpdateCartItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(auth.getName(), req));
    }

    @PostMapping("/remove")
    public ResponseEntity<CartResponse> remove(Authentication auth,
                                               @Valid @RequestBody RemoveCartItemRequest req) {
        return ResponseEntity.ok(cartService.removeItem(auth.getName(), req));
    }
}
