package com.example.demo.controller;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET CART 
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestParam @Positive(message = "userId must be greater than 0") Integer userId
    ) {
        CartResponse data = cartService.getCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart fetched successfully", data));
    }

    // ADD ITEM =
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponse>> addCartItem(
            @RequestParam @Positive(message = "userId must be greater than 0") Integer userId,
            @Valid @RequestBody CartItemRequest request
    ) {
        CartItemResponse data = cartService.addItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Cart item added", data));
    }

    // UPDATE ITEM
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(
            @PathVariable @Positive(message = "itemId must be greater than 0") Integer itemId,
            @Valid @RequestBody CartItemRequest request
    ) {
        CartItemResponse data = cartService.updateItem(itemId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart item updated", data));
    }

    // DELETE ITEM 
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable @Positive(message = "itemId must be greater than 0") Integer itemId,
            @RequestParam(required = false) @Positive(message = "userId must be greater than 0") Integer userId
    ) {
        cartService.removeItem(itemId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart item removed", null));
    }

    // CLEAR CART
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestParam @Positive(message = "userId must be greater than 0") Integer userId
    ) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared successfully", null));
    }

    // APPLY COUPON 
    @PostMapping("/apply-coupon")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(
            @RequestParam @Positive(message = "userId must be greater than 0") Integer userId,
            @Valid @RequestBody CouponRequest request
    ) {
        CartResponse data = cartService.applyCoupon(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Coupon applied to cart", data));
    }

    // REMOVE COUPON
    @DeleteMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponse>> removeCoupon(
            @RequestParam @Positive(message = "userId must be greater than 0") Integer userId
    ) {
        CartResponse data = cartService.removeCoupon(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Coupon removed from cart", data));
    }
}