package com.example.demo.service;

import com.example.demo.dto.response.WishlistResponse;
import com.example.demo.entity.Wishlist;
import com.example.demo.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;
    public List<WishlistResponse> getUserWishlist(Authentication auth) {
        String email = auth.getName();

        List<Wishlist> list =
                wishlistRepository.findByUser_Email(email);

        return list.stream()
                .map(item -> new WishlistResponse(
                        item.getId(),
                        item.getUser().getEmail()
                ))
                .toList();
    }
}
