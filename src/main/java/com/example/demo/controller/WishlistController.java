package com.example.demo.controller;

import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public WishlistResponse getWishlist(Authentication auth) {
        return wishlistService.getUserWishlist(auth);
    }
    @PostMapping("/add/{productId}")
    public void addToWishlist(@PathVariable Integer productId,
                              Authentication auth) {
        wishlistService.addToWishlist(productId, auth);
    }
    @DeleteMapping("/remove/{productId}")
    public void removeFromWishlist(@PathVariable Integer productId,
                                   Authentication auth) {
        wishlistService.removeFromWishlist(productId, auth);
    }

}