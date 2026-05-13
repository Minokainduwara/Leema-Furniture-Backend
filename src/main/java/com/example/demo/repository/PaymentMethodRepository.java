package com.example.demo.repository;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository
        extends JpaRepository<PaymentMethod, Integer> {

    List<PaymentMethod> findByUserId(Integer userId);
}