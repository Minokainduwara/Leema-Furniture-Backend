package com.example.demo.controller;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                userService.getAllUsers(role, status, search, page, size)
        );
    }
    // ================= PROFILE =================
    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }
    @PutMapping("/me")
    public UserResponse updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        return userService.updateProfile(authentication.getName(), request);
    }

    // ================= PASSWORD =================
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(authentication.getName(), request);

        return ResponseEntity.ok("Password updated successfully");
    }
}