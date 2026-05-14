package com.example.demo.controller;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CouponValidationResponse;
import com.example.demo.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

	private final CouponService couponService;

	public CouponController(CouponService couponService) {
		this.couponService = couponService;
	}

	@PostMapping("/validate")
	public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(@Valid @RequestBody CouponRequest request) {
		CouponValidationResponse data = couponService.validateCoupon(request);
		return ResponseEntity.ok(new ApiResponse<>(true, data.message(), data));
	}
}
