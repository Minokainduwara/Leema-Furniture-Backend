package com.example.demo.repository;

import com.example.demo.entity.CouponCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponCategoryRepository extends JpaRepository<CouponCategory, Integer> {
	List<CouponCategory> findByCouponId(Integer couponId);
	List<CouponCategory> findByCategoryId(Integer categoryId);
}
