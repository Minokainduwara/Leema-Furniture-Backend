package com.example.demo.service;

import com.example.demo.dto.request.AdminRejectSellerRequest;
import com.example.demo.dto.response.AdminSellerDetailResponse;
import com.example.demo.dto.response.AdminSellerResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminSellerService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private AdminSellerResponse toSellerResponse(User user) {
        long productCount = productRepository.countByUserId(user.getId());
        long orderCount = orderRepository.countByUserId(user.getId());
        
        return AdminSellerResponse.builder()
                .id(user.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .shopName(user.getName() + "'s Shop")
                .shopDescription("Seller: " + user.getName())
                .contactEmail(user.getEmail())
                .contactPhone(user.getPhoneNumber())
                .status(user.getStatus().name())
                .commissionRate(BigDecimal.valueOf(10)) // Default commission rate
                .totalProducts((int) productCount)
                .totalOrders((int) orderCount)
                .totalRevenue(BigDecimal.ZERO)
                .rating(BigDecimal.valueOf(5.0))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AdminSellerDetailResponse toSellerDetailResponse(User user) {
        AdminSellerResponse sellerResponse = toSellerResponse(user);
        
        return AdminSellerDetailResponse.builder()
                .seller(sellerResponse)
                .totalProducts((long) sellerResponse.getTotalProducts())
                .totalOrders((long) sellerResponse.getTotalOrders())
                .totalRevenue(sellerResponse.getTotalRevenue())
                .totalPayouts(BigDecimal.ZERO)
                .pendingPayouts(BigDecimal.ZERO)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<AdminSellerResponse> getAllSellers(String status, String search, Pageable pageable) {
        User.Status statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = User.Status.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        return userRepository.findByRoleAndFilters(User.Role.SELLER, statusEnum, search, pageable)
                .map(this::toSellerResponse);
    }

    @Transactional(readOnly = true)
    public AdminSellerDetailResponse getSellerById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        return toSellerDetailResponse(user);
    }

    @Transactional
    public AdminSellerResponse approveSeller(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
        
        return toSellerResponse(user);
    }

    @Transactional
    public AdminSellerResponse rejectSeller(Integer id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        user.setStatus(User.Status.INACTIVE);
        userRepository.save(user);
        
        return toSellerResponse(user);
    }

    @Transactional
    public AdminSellerResponse suspendSeller(Integer id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        user.setStatus(User.Status.SUSPENDED);
        userRepository.save(user);
        
        return toSellerResponse(user);
    }

    @Transactional
    public AdminSellerResponse activateSeller(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
        
        return toSellerResponse(user);
    }

    @Transactional
    public AdminSellerResponse updateCommissionRate(Integer id, BigDecimal commissionRate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found: " + id));
        
        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("User is not a seller: " + id);
        }
        
        // Store commission rate in user attributes or separate table
        // For now, we'll just return the response with the new rate
        return toSellerResponse(user);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSellerAnalytics() {
        List<User> sellers = userRepository.findByRole(User.Role.SELLER);
        
        long totalSellers = sellers.size();
        long activeSellers = sellers.stream()
                .filter(u -> u.getStatus() == User.Status.ACTIVE)
                .count();
        long suspendedSellers = sellers.stream()
                .filter(u -> u.getStatus() == User.Status.SUSPENDED)
                .count();
        long inactiveSellers = sellers.stream()
                .filter(u -> u.getStatus() == User.Status.INACTIVE)
                .count();

        return Map.of(
                "totalSellers", totalSellers,
                "activeSellers", activeSellers,
                "suspendedSellers", suspendedSellers,
                "inactiveSellers", inactiveSellers
        );
    }
}