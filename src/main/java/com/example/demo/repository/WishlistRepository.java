package com.example.demo.repository;

import com.example.demo.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist,Integer> {
    int countByUser_Email(String email);
    List<Wishlist> findByUser_Email(String email);
}