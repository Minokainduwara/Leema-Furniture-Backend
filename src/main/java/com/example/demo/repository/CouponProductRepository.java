package com.example.demo.repository;

import com.example.demo.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
	Optional<Coupon> findByCode(String code);

	Optional<Coupon> findByCodeAndIsActiveTrue(String code);

	boolean existsByCode(String code);
}
