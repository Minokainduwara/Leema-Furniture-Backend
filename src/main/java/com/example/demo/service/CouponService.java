package com.example.demo.service;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponValidationResponse;
import com.example.demo.entity.Coupon;
import com.example.demo.repository.CouponCategoryRepository;
import com.example.demo.repository.CouponProductRepository;
import com.example.demo.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {

	private final CouponRepository couponRepository;
	private final CouponProductRepository couponProductRepository;
	private final CouponCategoryRepository couponCategoryRepository;

	public CouponService(
			CouponRepository couponRepository,
			CouponProductRepository couponProductRepository,
			CouponCategoryRepository couponCategoryRepository
	) {
		this.couponRepository = couponRepository;
		this.couponProductRepository = couponProductRepository;
		this.couponCategoryRepository = couponCategoryRepository;
	}

	public CouponValidationResponse validateCoupon(CouponRequest request) {
		if (request == null || request.couponCode() == null || request.couponCode().isBlank()) {
			throw new IllegalArgumentException("couponCode is required");
		}

		Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.couponCode())
				.orElseThrow(() -> new IllegalArgumentException("Coupon is invalid or inactive"));

		BigDecimal subtotal = request.subtotal() == null ? BigDecimal.ZERO : request.subtotal();
		ensureCouponIsUsable(coupon, subtotal);
		ensureCouponAppliesToRequest(coupon, request.productIds(), request.categoryIds());

		BigDecimal discountAmount = calculateDiscountAmount(coupon, subtotal);
		if (discountAmount.compareTo(subtotal) > 0) {
			discountAmount = subtotal;
		}

		BigDecimal totalAfterDiscount = subtotal.subtract(discountAmount);
		if (totalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
			totalAfterDiscount = BigDecimal.ZERO;
		}

		return new CouponValidationResponse(
				coupon.getCode(),
				true,
				"Coupon is valid",
				coupon.getDiscountType() == null ? null : coupon.getDiscountType().name().toLowerCase(),
				coupon.getDiscountValue(),
				discountAmount,
				totalAfterDiscount
		);
	}

	private void ensureCouponIsUsable(Coupon coupon, BigDecimal subtotal) {
		LocalDate today = LocalDate.now();
		if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
			throw new IllegalArgumentException("Coupon is not yet valid");
		}
		if (coupon.getValidUntil() != null && today.isAfter(coupon.getValidUntil())) {
			throw new IllegalArgumentException("Coupon has expired");
		}
		if (coupon.getMaxUses() != null && coupon.getCurrentUses() != null && coupon.getCurrentUses() >= coupon.getMaxUses()) {
			throw new IllegalArgumentException("Coupon usage limit has been reached");
		}
		if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
			throw new IllegalArgumentException("Order amount does not meet the minimum required for this coupon");
		}
		if (coupon.getMaxOrderAmount() != null && subtotal.compareTo(coupon.getMaxOrderAmount()) > 0) {
			throw new IllegalArgumentException("Order amount exceeds the maximum allowed for this coupon");
		}
	}

	private void ensureCouponAppliesToRequest(Coupon coupon, List<Integer> productIds, List<Integer> categoryIds) {
		if (coupon.getApplicableTo() == null || coupon.getApplicableTo() == Coupon.ApplicableTo.ALL_PRODUCTS) {
			return;
		}

		if (coupon.getApplicableTo() == Coupon.ApplicableTo.SPECIFIC_PRODUCTS) {
			List<Integer> allowedProductIds = couponProductRepository.findByCouponId(coupon.getId()).stream()
					.map(com.example.demo.entity.CouponProduct::getProductId)
					.filter(java.util.Objects::nonNull)
					.toList();
			if (allowedProductIds.isEmpty()) {
				throw new IllegalArgumentException("Coupon has no product mappings");
			}
			List<Integer> requestedProductIds = productIds == null ? List.of() : new ArrayList<>(productIds);
			boolean matches = requestedProductIds.stream().anyMatch(allowedProductIds::contains);
			if (!matches) {
				throw new IllegalArgumentException("Coupon does not apply to the selected products");
			}
			return;
		}

		List<Integer> allowedCategoryIds = couponCategoryRepository.findByCouponId(coupon.getId()).stream()
				.map(com.example.demo.entity.CouponCategory::getCategoryId)
				.filter(java.util.Objects::nonNull)
				.toList();
		if (allowedCategoryIds.isEmpty()) {
			throw new IllegalArgumentException("Coupon has no category mappings");
		}
		List<Integer> requestedCategoryIds = categoryIds == null ? List.of() : new ArrayList<>(categoryIds);
		boolean matches = requestedCategoryIds.stream().anyMatch(allowedCategoryIds::contains);
		if (!matches) {
			throw new IllegalArgumentException("Coupon does not apply to the selected categories");
		}
	}

	private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal subtotal) {
		if (coupon.getDiscountType() == null || coupon.getDiscountValue() == null) {
			return BigDecimal.ZERO;
		}

		return switch (coupon.getDiscountType()) {
			case PERCENTAGE -> subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
			case FIXED_AMOUNT -> coupon.getDiscountValue();
		};
	}
}
