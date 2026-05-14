package com.example.demo.controller;

import com.example.demo.dto.response.SellerDashboardResponse;
import com.example.demo.service.SellerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final SellerDashboardService dashboardService;

    @GetMapping("/seller")
    public SellerDashboardResponse getDashboard() {
        return dashboardService.getDashboardData();
    }
}