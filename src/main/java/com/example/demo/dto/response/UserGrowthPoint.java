package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserGrowthPoint {
    private LocalDate date;
    private Long newUsers;
    private Long totalUsers;
}
