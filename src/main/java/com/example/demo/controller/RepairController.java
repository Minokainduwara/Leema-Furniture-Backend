package com.example.demo.controller;

import com.example.demo.entity.Repair;
import com.example.demo.entity.User;
import com.example.demo.enums.RepairStatus;
import com.example.demo.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
@CrossOrigin(origins = "http://localhost:5173")
public class RepairController {

    @Autowired
    private RepairService repairService;

    // CREATE REPAIR (customer)
    @PostMapping
    public Repair createRepair(@RequestBody Repair repair) {
        return repairService.createRepair(repair);
    }

    // GET ALL (admin)
    @GetMapping("/all")
    public List<Repair> getAllRepairs() {
        return repairService.getAllRepairs();
    }

    // CUSTOMER REPAIRS
    @GetMapping("/user/{userId}")
    public List<Repair> getUserRepairs(@PathVariable Integer userId) {
        return repairService.getRepairsByUser(userId);
    }

    // SELLER REPAIRS
    @GetMapping("/seller/{sellerId}")
    public List<Repair> getSellerRepairs(@PathVariable Integer sellerId) {
        return repairService.getRepairsBySeller(sellerId);
    }

    // ASSIGN REPAIR TO SELLER
    @PutMapping("/{repairId}/assign")
    public Repair assignRepair(@PathVariable Integer repairId,
                               @RequestBody User seller) {
        return repairService.assignRepair(repairId, seller);
    }

    // UPDATE STATUS
    @PatchMapping("/{repairId}/status")
    public Repair updateStatus(@PathVariable Integer repairId,
                               @RequestParam RepairStatus status) {
        return repairService.updateStatus(repairId, status);
    }
}