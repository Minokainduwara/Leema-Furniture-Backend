package com.example.demo.service;

import com.example.demo.dto.request.AdminUpdateUserStatusRequest;
import com.example.demo.dto.response.AdminUserResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.AdminLog;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AdminLogRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserActivityLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AdminLogRepository adminLogRepository;
    private final UserActivityLogRepository userActivityLogRepository;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;


    public Page<AdminUserResponse> getAllUsers(String role, String status, String search, Pageable pageable) {
        return userRepository.findWithFilters(role, status, search, pageable)
                .map(this::toResponse);
    }

    public AdminUserResponse getUserById(Integer id) {
        User user = findUser(id);
        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(Integer id, AdminUpdateUserStatusRequest request) {
        User user = findUser(id);
        User admin = securityUtils.getCurrentUser();

        Map<String, Object> oldValues = Map.of("status", user.getStatus());
        user.setStatus(User.Status.valueOf(request.getStatus().toUpperCase()));
        userRepository.save(user);

        logAction(admin, "UPDATE_STATUS", "USER", id, oldValues,
                Map.of("status", user.getStatus()));

        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse updateUserRole(Integer id, String role) {
        User user = findUser(id);
        User admin = securityUtils.getCurrentUser();

        Map<String, Object> oldValues = Map.of("role", user.getRole());
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);

        logAction(admin, "UPDATE_ROLE", "USER", id, oldValues,
                Map.of("role", user.getRole()));

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = findUser(id);
        User admin = securityUtils.getCurrentUser();

        user.setStatus(User.Status.deleted);
        userRepository.save(user);

        logAction(admin, "DELETE", "USER", id, Map.of("status", "ACTIVE"), Map.of("status", "DELETED"));
    }

    public Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable) {
        findUser(userId); // validate exists
        return orderRepository.findByUserId(userId, pageable)
                .map(order -> OrderResponse.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .status(order.getStatus().name())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt())
                        .build());
    }

    public Page<?> getUserActivity(Integer userId, Pageable pageable) {
        findUser(userId);
        return userActivityLogRepository.findByUserId(userId, pageable);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User findUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }



    private void logAction(User admin, String action, String entityType, Integer entityId,
                           Map<String, Object> oldValues, Map<String, Object> newValues) {

        try {
            String oldJson = objectMapper.writeValueAsString(oldValues);
            String newJson = objectMapper.writeValueAsString(newValues);

            adminLogRepository.save(AdminLog.builder()
                    .admin(admin)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValues(oldJson)
                    .newValues(newJson)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize admin log", e);
        }
    }

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
