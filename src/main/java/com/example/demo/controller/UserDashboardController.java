package com.example.demo.controller;

import com.example.demo.dto.response.UserDashboardResponse;
import com.example.demo.service.UserDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class UserDashboardController {

    @Autowired
    private UserDashboardService userDashboardService;

    @GetMapping("/overview")
    public UserDashboardResponse getOverview(Authentication auth) {
        return userDashboardService.getOverview(auth.getName());
    }
}
