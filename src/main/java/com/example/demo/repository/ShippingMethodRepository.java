package com.example.demo.repository;

import com.example.demo.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Integer> {
    List<ShippingMethod> findByIsActiveTrue();
}
