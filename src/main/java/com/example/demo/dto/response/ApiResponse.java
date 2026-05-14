package com.example.demo.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer status;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ── Static factory methods ────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .status(200)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(200)
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .status(200)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .status(201)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Integer status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(message, 400);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, 404);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, 401);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return error(message, 403);
    }

    public static <T> ApiResponse<T> serverError(String message) {
        return error(message, 500);
    }
}
