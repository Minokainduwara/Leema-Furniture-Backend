package com.example.demo.service;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.RefreshToken;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .role(User.Role.CUSTOMER)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);

        // Auto login
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse refresh(String refreshTokenStr) {

        // 1. Validate existing token
        RefreshToken oldToken = refreshTokenService.verifyToken(refreshTokenStr);

        User user = oldToken.getUser();

        // 2. Revoke old token
        refreshTokenService.revokeToken(oldToken);

        // 3. Create new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        // 4. Generate new access token
        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(), // NEW TOKEN
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void logout(String refreshTokenStr) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyToken(refreshTokenStr);
            refreshTokenService.revokeToken(refreshToken);
        } catch (Exception ex) {
            // already invalid or expired → ignore
        }
    }
}