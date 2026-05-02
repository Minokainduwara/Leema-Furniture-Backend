package com.example.demo.controller;

import com.example.demo.entity.Repair;
import com.example.demo.entity.User;
import com.example.demo.enums.RepairStatus;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // CUSTOMER - CREATE REPAIR
    // =========================
    @PostMapping
    public Repair create(@RequestBody Repair repair) {
        return repairService.createRepair(repair);
    }

    // =========================
    // ADMIN - GET ALL REPAIRS
    // =========================
    @GetMapping("/admin")
    public List<Repair> getAll() {
        return repairService.getAllRepairs();
    }

    // =========================
    // CUSTOMER - MY REPAIRS
    // (TEMP USER ID = 1)
    // =========================
    @GetMapping("/my/{userId}")
    public List<Repair> getMyRepairs(@PathVariable Integer userId) {
        return repairService.getRepairsByUser(userId);
    }

    // =========================
    // SELLER - ASSIGNED REPAIRS
    // =========================
    @GetMapping("/seller/{sellerId}")
    public List<Repair> getSellerRepairs(@PathVariable Integer sellerId) {
        return repairService.getRepairsBySeller(sellerId);
    }

    // =========================
    // ASSIGN REPAIR (ADMIN)
    // =========================
    @PatchMapping("/{repairId}/assign/{sellerId}")
    public Repair assignRepair(@PathVariable Integer repairId,
                               @PathVariable Integer sellerId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return repairService.assignRepair(repairId, seller);
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @PatchMapping("/{id}/status")
    public Repair updateStatus(@PathVariable Integer id,
                               @RequestParam String status) {

        return repairService.updateStatus(id, RepairStatus.valueOf(status));
    }
}