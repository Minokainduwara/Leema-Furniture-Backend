package com.example.demo.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private Integer id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profilePicture;

    private String role;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
