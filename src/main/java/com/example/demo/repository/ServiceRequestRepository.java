package com.example.demo.repository;

import com.example.demo.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    int countByUser_Email(String email);
    List<ServiceRequest> findByUser_Email(String email);
}