package com.example.demo.controller;

import com.example.demo.dto.request.WishlistItemRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.WishlistItemResponse;
import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.service.WishlistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/wishlist")
@Validated
public class WishlistController {

	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(@RequestParam @Positive(message = "userId must be greater than 0") Integer userId) {
		WishlistResponse data = wishlistService.getWishlist(userId);
		return ResponseEntity.ok(new ApiResponse<>(true, "Wishlist fetched successfully", data));
	}

	@PostMapping("/items")
	public ResponseEntity<ApiResponse<WishlistItemResponse>> addWishlistItem(
			@RequestParam @Positive(message = "userId must be greater than 0") Integer userId,
			@Valid @RequestBody WishlistItemRequest request
	) {
		WishlistItemResponse data = wishlistService.addItem(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Wishlist item added", data));
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<ApiResponse<Void>> removeWishlistItem(
			@RequestParam @Positive(message = "userId must be greater than 0") Integer userId,
			@PathVariable @Positive(message = "productId must be greater than 0") Integer productId
	) {
		wishlistService.removeItem(userId, productId);
		return ResponseEntity.ok(new ApiResponse<>(true, "Wishlist item removed", null));
	}

	@PostMapping("/items/{productId}/move-to-cart")
	public ResponseEntity<ApiResponse<Void>> moveWishlistItemToCart(
			@RequestParam @Positive(message = "userId must be greater than 0") Integer userId,
			@PathVariable @Positive(message = "productId must be greater than 0") Integer productId
	) {
		wishlistService.moveItemToCart(userId, productId);
		return ResponseEntity.ok(new ApiResponse<>(true, "Wishlist item moved to cart", null));
	}
}
