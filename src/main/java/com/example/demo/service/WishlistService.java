package com.example.demo.service;

import com.example.demo.dto.response.WishlistItemResponse;
import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.Wishlist;
import com.example.demo.entity.WishlistItem;
import com.example.demo.entity.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // GET WISHLIST
    // =========================
    public WishlistResponse getUserWishlist(Authentication auth) {

        String email = auth.getName();

        Wishlist wishlist = getOrCreateWishlist(email);

        List<WishlistItemResponse> items = wishlist.getItems()
                .stream()
                .map(item -> new WishlistItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getProduct().getImage(),
                        item.getProduct().getDescription(),
                        item.getProduct().getCategory().getName(),
                        item.getProduct().getSku()
                ))
                .toList();

        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getUser().getEmail(),
                items
        );
    }

    // =========================
    // ADD TO WISHLIST
    // =========================
    public void addToWishlist(Integer productId, Authentication auth) {

        String email = auth.getName();

        Wishlist wishlist = getOrCreateWishlist(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean exists = wishlist.getItems()
                .stream()
                .anyMatch(i -> i.getProduct().getId().equals(productId));

        if (exists) {
            return; // or throw if you want strict behavior
        }

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        wishlist.getItems().add(item);

        wishlistRepository.save(wishlist);
    }

    // =========================
    // REMOVE FROM WISHLIST
    // =========================
    public void removeFromWishlist(Integer productId, Authentication auth) {

        String email = auth.getName();

        Wishlist wishlist = getOrCreateWishlist(email);

        WishlistItem item = wishlist.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in wishlist"));

        wishlist.getItems().remove(item);

        wishlistRepository.save(wishlist);
    }

    // =========================
    // COMMON HELPER (IMPORTANT)
    // =========================
    private Wishlist getOrCreateWishlist(String email) {

        return wishlistRepository.findByUser_Email(email)
                .orElseGet(() -> {

                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);

                    return wishlistRepository.save(wishlist);
                });
    }
}