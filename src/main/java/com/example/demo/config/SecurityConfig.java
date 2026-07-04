package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // OPTIONS



                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // AUTH
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/repairs/**")

                        .hasAnyRole("ADMIN", "SELLER", "CUSTOMER")
                        // PUBLIC GET APIs
                        .requestMatchers("/api/service-requests/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // CATEGORY MANAGEMENT (ADMIN + SELLER)
                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/checkout").authenticated()
                        .requestMatchers("/api/wishlist/**")
                        .authenticated()

                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasAnyRole("ADMIN", "SELLER")

                        // USER APIs
                        .requestMatchers("/api/dashboard/**").authenticated()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        // FILES
                        .requestMatchers("/uploads/**").permitAll()

                        // PAYHERE
                        .requestMatchers("/api/payments/payhere/notify").permitAll()

                        // SWAGGER
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}