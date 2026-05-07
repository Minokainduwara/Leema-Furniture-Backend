//package com.example.demo.repository;
//
//import com.example.demo.entity.Coupon;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface CouponProductRepository extends JpaRepository<Coupon, Integer> {
//	Optional<Coupon> findByCode(String code);
//
//	Optional<Coupon> findByCodeAndIsActiveTrue(String code);
//
//	boolean existsByCode(String code);
//}
package com.example.demo.repository;

import com.example.demo.entity.CouponProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponProductRepository extends JpaRepository<CouponProduct, Integer> {

    List<CouponProduct> findByCoupon_Id(Integer couponId);
}