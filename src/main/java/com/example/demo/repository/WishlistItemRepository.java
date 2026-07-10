package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.Wishlist;
import com.example.demo.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {

    // ✅ Get all items in a wishlist
    List<WishlistItem> findByWishlist(Wishlist wishlist);

    // ✅ Check if product already exists in wishlist
    Optional<WishlistItem> findByWishlistAndProduct(Wishlist wishlist, Product product);

    // ✅ Remove a product from wishlist
    void deleteByWishlistAndProduct(Wishlist wishlist, Product product);

    // ✅ Count items (for badge icon ❤️)
    int countByWishlist(Wishlist wishlist);
}
