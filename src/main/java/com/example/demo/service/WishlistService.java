package com.example.demo.service;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.WishlistItemRequest;
import com.example.demo.dto.response.WishlistItemResponse;
import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.Wishlist;
import com.example.demo.entity.WishlistItem;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WishlistItemRepository;
import com.example.demo.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            ProductRepository productRepository,
            CartService cartService
    ) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            return new WishlistResponse(null, List.of());
        }

        return buildWishlistResponse(wishlist);
    }

    @Transactional
    public WishlistItemResponse addItem(Integer userId, WishlistItemRequest request) {
        if (userId == null || request == null || request.productId() == null) {
            throw new IllegalArgumentException("userId and productId are required");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Wishlist wishlist = getOrCreateWishlist(userId);
        LocalDateTime now = LocalDateTime.now();

        WishlistItem existingItem = wishlistItemRepository
                .findByWishlistIdAndProductId(wishlist.getId(), request.productId())
                .orElse(null);

        if (existingItem != null) {
            return toResponse(existingItem, product.getName());
        }

        WishlistItem item = new WishlistItem();
        item.setWishlist(wishlist);
        item.setProductId(request.productId());
        item.setAddedAt(now);

        WishlistItem saved = wishlistItemRepository.save(item);

        wishlist.setUpdatedAt(now);
        wishlistRepository.save(wishlist);

        return toResponse(saved, product.getName());
    }

    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            throw new IllegalArgumentException("userId and productId are required");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId).orElse(null);
        if (wishlist == null) {
            return;
        }

        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void moveItemToCart(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            throw new IllegalArgumentException("userId and productId are required");
        }

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        WishlistItem item = wishlistItemRepository
                .findByWishlistIdAndProductId(wishlist.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // ✅ FIXED LINE
        cartService.addItem(
                userId,
                new CartItemRequest(productId, 1, product.getPrice())
        );

        wishlistItemRepository.delete(item);

        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);
    }

    private Wishlist getOrCreateWishlist(Integer userId) {
        return wishlistRepository.findByUserId(userId).orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            Wishlist wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist.setCreatedAt(now);
            wishlist.setUpdatedAt(now);
            return wishlistRepository.save(wishlist);
        });
    }

    private WishlistResponse buildWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> items = new ArrayList<>();

        for (WishlistItem item : wishlist.getItems()) {
            items.add(toResponse(
                    item,
                    productRepository.findById(item.getProductId())
                            .map(Product::getName)
                            .orElse(null)
            ));
        }

        return new WishlistResponse(wishlist.getId(), items);
    }

    private WishlistItemResponse toResponse(WishlistItem item, String productName) {
        return new WishlistItemResponse(
                item.getId(),
                item.getProductId(),
                productName,
                item.getAddedAt()
        );
    }
}