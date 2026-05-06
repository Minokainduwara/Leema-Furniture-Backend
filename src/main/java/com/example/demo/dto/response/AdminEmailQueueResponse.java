package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminEmailQueueResponse {
    private Integer id;
    private Integer userId;
    private String email;
    private String subject;
    private String template;
    private String status;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
