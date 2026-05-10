package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.RemoveCartItemRequest;
import com.example.demo.dto.request.UpdateCartRequest;
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

    // =========================================================
    // ADD TO CART
    // =========================================================

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication
    ) {

        cartService.addToCart(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok("Item added to cart");
    }

    // =========================================================
    // GET CART
    // =========================================================

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {

        CartResponse response = cartService.getCart(
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE CART ITEM
    // =========================================================

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(
            @Valid @RequestBody UpdateCartRequest request,
            Authentication authentication
    ) {

        cartService.updateCartItem(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok("Cart updated");
    }

    // =========================================================
    // REMOVE CART ITEM
    // =========================================================

    @DeleteMapping("/item")
    public ResponseEntity<String> removeCartItem(
            @Valid @RequestBody RemoveCartItemRequest request,
            Authentication authentication
    ) {

        cartService.removeCartItem(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok("Item removed from cart");
    }
}