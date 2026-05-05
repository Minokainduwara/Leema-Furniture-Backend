package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminUserAnalyticsResponse {
    private Long totalUsers;
    private Long newUsersThisMonth;
    private Long activeUsers;
    private Long suspendedUsers;
    private List<UserGrowthPoint> growthData;
}
