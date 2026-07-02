package com.example.demo.controller;

import com.example.demo.dto.request.RepairRequest;
import com.example.demo.entity.Repair;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
@CrossOrigin
public class RepairController {

    @Autowired
    private RepairService repairService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Repair createRepair(@RequestBody RepairRequest body, Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repairService.createRepair(

                body.getOrderNumber(),
                body.getSku(),
                body.getIssueDescription(),
                null, // estimatedCost = null for user
                Repair.ServiceType.valueOf(body.getType().toUpperCase())
        );
    }
    @GetMapping("/seller/pending")
    public List<Repair> getPendingSellerRepairs() {
        return repairService.getRepairsByStatus("REQUESTED");
    }
    @PatchMapping("/{id}/update")
    public Repair updateRepair(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {

        return repairService.updateRepair(
                id,
                (String) body.get("status"),
                body.get("estimatedCost") != null ? Double.valueOf(body.get("estimatedCost").toString()) : null
        );
    }
    @GetMapping
    public List<Repair> getAll() {
        return repairService.getAll();
    }

    @GetMapping("/order-user/{orderId}")
    public List<Repair> getRepairsByOrderUser(@PathVariable Integer orderId) {
        return repairService.getRepairsByOrderUser(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<Repair> getByUser(@PathVariable Integer userId) {
        return repairService.getByUser(userId);
    }

    @GetMapping("/order/{orderId}")
    public List<Repair> getByOrder(@PathVariable Integer orderId) {
        return repairService.getByOrder(orderId);
    }

    @GetMapping("/product/{productId}")
    public List<Repair> getByProduct(@PathVariable Integer productId) {
        return repairService.getByProduct(productId);
    }

    @GetMapping("/status")
    public List<Repair> getByStatus(@RequestParam String status) {
        return repairService.getByStatus(status);
    }


    @PatchMapping("/{id}/status")
    public Repair updateStatus(@PathVariable Integer id,
                               @RequestBody Map<String, String> body) {

        return repairService.updateStatus(id, body.get("status"));
    }
    private Integer getSellerId(Authentication auth) {

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.Role.SELLER) {
            throw new RuntimeException("Access denied: not a seller");
        }

        return user.getId();
    }
    @GetMapping("/seller")
    public List<Repair> getSellerRequests(Authentication auth) {

        Integer sellerId = getSellerId(auth); // or extract from user

        return repairService.getSellerRequests(sellerId);
    }
}