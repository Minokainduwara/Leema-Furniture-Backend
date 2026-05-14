package com.example.demo.service;

import com.example.demo.dto.response.ServiceRequestResponse;
import com.example.demo.entity.ServiceRequest;
import com.example.demo.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public List<ServiceRequestResponse> getUserRequests(Authentication auth) {

        String email = auth.getName();

        List<ServiceRequest> requests =
                serviceRequestRepository.findByUser_Email(email);

        return requests.stream()
                .map(req -> new ServiceRequestResponse(
                        req.getId(),
                        req.getStatus(),
                        req.getCreatedAt().toString(),
                        "SR-" + req.getId()
                ))
                .toList();
    }
}