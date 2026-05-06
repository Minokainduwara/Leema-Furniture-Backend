package com.example.demo.repository;

import com.example.demo.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Integer> {

    List<ShippingAddress> findByUserId(Integer userId);

    void deleteByUserIdAndId(Integer userId, Integer id);
}