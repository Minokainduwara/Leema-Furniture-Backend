package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AdminSettingResponse {
    private Integer id;
    private String settingKey;
    private Map<String, Object> settingValue;
    private String description;
    private LocalDateTime updatedAt;
}
