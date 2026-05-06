package com.example.demo.controller;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.RateLimitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RateLimitService rateLimitService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(
			@Valid @RequestBody RegisterRequest req,
			jakarta.servlet.http.HttpServletRequest request) {

		String clientIp = request.getRemoteAddr();
		rateLimitService.validateRequest(clientIp);

		return ResponseEntity.ok(authService.register(req));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@Valid @RequestBody LoginRequest req,
			jakarta.servlet.http.HttpServletRequest request) {

		String clientIp = request.getRemoteAddr();

		// Rate limit check
		rateLimitService.validateRequest(clientIp);

		AuthResponse response = authService.login(req.getEmail(), req.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
		System.out.println("REFRESH ENDPOINT HIT");
		return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.ok().body("Logged out successfully");
	}
}