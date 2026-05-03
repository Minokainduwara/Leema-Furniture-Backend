package com.example.demo.repository;

import com.example.demo.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {

	Optional<WishlistItem> findByWishlistIdAndProductId(Integer wishlistId, Integer productId);

	List<WishlistItem> findByWishlistId(Integer wishlistId);

	void deleteByWishlistIdAndProductId(Integer wishlistId, Integer productId);
}