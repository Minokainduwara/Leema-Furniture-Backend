package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist,Integer> {
    int countByUser_Email(String email);
    Optional<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUser_Email(String email);
    boolean existsByUser(User user);
}
