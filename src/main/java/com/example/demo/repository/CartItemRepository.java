package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

	Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
	List<CartItem> findByCart(Cart cart);
}
