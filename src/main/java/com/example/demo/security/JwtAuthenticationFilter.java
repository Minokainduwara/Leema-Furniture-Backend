package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");

        System.out.println("========== JWT FILTER ==========");
        System.out.println("📍 Path: " + path);
        System.out.println("🔐 Auth Header: " + authHeader);

        // Skip auth endpoints
        if (path.startsWith("/api/auth")) {
            System.out.println("✅ Auth endpoint - skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("🎫 Token extracted: " + token.substring(0, 20) + "...");
        } else {
            System.out.println("❌ NO TOKEN FOUND IN HEADER!");
            filterChain.doFilter(request, response);
            return;
        }

        // Check if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("⚠️ Already authenticated, skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Validate token
            if (token == null || !jwtService.isValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (!jwtService.isValid(token)) {
                System.out.println("❌ JWT VALIDATION FAILED");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            System.out.println("✅ JWT validation passed");

            // Extract email
            String email = jwtService.extractEmail(token);
            System.out.println("👤 Email from token: " + email);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            System.out.println("✅ User loaded: " + userDetails.getUsername());
            System.out.println("👁️ Enabled: " + userDetails.isEnabled());
            System.out.println("🔓 Account non-locked: " + userDetails.isAccountNonLocked());
            System.out.println("🗝️ Credentials non-expired: " + userDetails.isCredentialsNonExpired());
            System.out.println("🛡️ Authorities: " + userDetails.getAuthorities());

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("AUTH OBJECT = " +
                    SecurityContextHolder.getContext().getAuthentication()
            );
            System.out.println("✅ AUTHENTICATION SET SUCCESSFULLY");

        } catch (Exception ex) {
            System.out.println("❌ EXCEPTION IN JWT FILTER: " + ex.getClass().getSimpleName());
            System.out.println("❌ ERROR MESSAGE: " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("================================");
        filterChain.doFilter(request, response);
    }
}