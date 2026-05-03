package com.example.demo.repository;

import com.example.demo.entity.BillingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingAddressRepository extends JpaRepository<BillingAddress, Integer> {

    List<BillingAddress> findByUserId(Integer userId);

    void deleteByUserIdAndId(Integer userId, Integer id);
}