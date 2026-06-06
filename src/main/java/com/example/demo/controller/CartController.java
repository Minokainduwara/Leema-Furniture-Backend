package com.example.demo.controller;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;



    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addToCart(
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItemResponse response = cartService.addItem(
                user.getId(),
                request
        );

        return ResponseEntity.ok(response);
    }



    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartResponse response = cartService.getCart(
                user.getId()
        );

        return ResponseEntity.ok(response);
    }



    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Integer itemId,
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItemResponse response = cartService.updateItem(
                itemId,
                request
        );

        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable Integer itemId,
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeItem(
                itemId,
                user.getId()
        );

        return ResponseEntity.ok("Item removed from cart");
    }


    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            Authentication authentication
    ) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.clearCart(user.getId());

        return ResponseEntity.ok("Cart cleared");
    }



    }