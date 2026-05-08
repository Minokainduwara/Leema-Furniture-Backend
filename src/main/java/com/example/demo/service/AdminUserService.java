package com.example.demo.service;

import com.example.demo.dto.request.AdminUpdateUserStatusRequest;
import com.example.demo.dto.response.AdminUserResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserActivityLogRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserActivityLogRepository userActivityLogRepository;

    // ─────────────────────────────────────────────
    // SAFE ENUM PARSERS
    // ─────────────────────────────────────────────
    private User.Role parseRole(String role) {
        if (role == null || role.isBlank()) return null;

        try {
            return User.Role.valueOf(role.trim().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    private User.Status parseStatus(String status) {
        if (status == null || status.isBlank()) return null;

        try {
            return User.Status.valueOf(status.trim().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    // ─────────────────────────────────────────────
    // GET ALL USERS (FIXED)
    // ─────────────────────────────────────────────
    public Page<AdminUserResponse> getAllUsers(String role, String status, String search, Pageable pageable) {

        User.Role roleEnum = parseRole(role);
        User.Status statusEnum = parseStatus(status);

        return userRepository.findWithFilters(roleEnum, statusEnum, search, pageable)
                .map(this::toResponse);
    }

    // ─────────────────────────────────────────────
    // GET USER BY ID
    // ─────────────────────────────────────────────
    public AdminUserResponse getUserById(Integer id) {
        return toResponse(findUser(id));
    }

    // ─────────────────────────────────────────────
    // UPDATE STATUS (FIXED)
    // ─────────────────────────────────────────────
    @Transactional
    public AdminUserResponse updateUserStatus(Integer id, AdminUpdateUserStatusRequest request) {

        User user = findUser(id);

        user.setStatus(parseStatus(request.getStatus()));

        userRepository.save(user);
        return toResponse(user);
    }

    // ─────────────────────────────────────────────
    // UPDATE ROLE (FIXED)
    // ─────────────────────────────────────────────
    @Transactional
    public AdminUserResponse updateUserRole(Integer id, String role) {

        User user = findUser(id);

        user.setRole(parseRole(role));

        userRepository.save(user);
        return toResponse(user);
    }

    // ─────────────────────────────────────────────
    // DELETE USER (SOFT DELETE FIXED)
    // ─────────────────────────────────────────────
    @Transactional
    public void deleteUser(Integer id) {

        User user = findUser(id);

        user.setStatus(User.Status.deleted);
        userRepository.save(user);
    }

    // ─────────────────────────────────────────────
    // USER ORDERS
    // ─────────────────────────────────────────────
    public Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable) {

        findUser(userId);

        return orderRepository.findByUserId(userId, pageable)
                .map(order -> OrderResponse.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .status(order.getStatus().name())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt())
                        .build());
    }

    // ─────────────────────────────────────────────
    // USER ACTIVITY
    // ─────────────────────────────────────────────
    public Page<?> getUserActivity(Integer userId, Pageable pageable) {
        findUser(userId);
        return userActivityLogRepository.findByUserId(userId, pageable);
    }

    // ─────────────────────────────────────────────
    // FIND USER (SAFE)
    // ─────────────────────────────────────────────
    private User findUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    // ─────────────────────────────────────────────
    // RESPONSE MAPPER
    // ─────────────────────────────────────────────
    private AdminUserResponse toResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}