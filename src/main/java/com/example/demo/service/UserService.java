package com.example.demo.service;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.UpdateProfileRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.dto.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Page<UserResponse> getAllUsers(
            String role,
            String status,
            String search,
            int page,
            int size
    ) {

        User.Role roleEnum = null;
        User.Status statusEnum = null;

        if (role != null) {
            roleEnum = User.Role.valueOf(role.toUpperCase());
        }

        if (status != null) {
            statusEnum = User.Status.valueOf(status.toUpperCase());
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.findWithFilters(
                roleEnum,
                statusEnum,
                search,
                pageable
        );

        return users.map(this::mapToResponse);
    }
    public UserResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse res = new UserResponse();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setRole(user.getRole().name());

        return res;
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(req.getName());
        user.setPhoneNumber(req.getPhoneNumber());

        // ⚠️ email update (check duplicate)
        if (!user.getEmail().equals(req.getEmail())) {

            if (userRepository.existsByEmail(req.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            user.setEmail(req.getEmail());
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changePassword(String email, ChangePasswordRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. check current password
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 2. validate new password
        if (req.getNewPassword().length() < 6) {
            throw new RuntimeException("Password too weak");
        }

        // 3. update password
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));

        userRepository.save(user);
    }
}
