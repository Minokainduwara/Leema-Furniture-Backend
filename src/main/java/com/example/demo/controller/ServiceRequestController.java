package com.example.demo.controller;

import com.example.demo.dto.response.ServiceRequestResponse;
import com.example.demo.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping
    public List<ServiceRequestResponse> getUserRequests(Authentication auth) {
        return serviceRequestService.getUserRequests(auth);
    }
}