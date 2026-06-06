package com.example.demo.controller;

import com.example.demo.dto.request.RepairRequest;
import com.example.demo.entity.Repair;
import com.example.demo.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
@CrossOrigin
public class RepairController {

    @Autowired
    private RepairService repairService;


    @PostMapping
    public Repair createRepair(@RequestBody RepairRequest body) {

        return repairService.createRepair(
                body.getUserId(),
                body.getProductId(),
                body.getOrderId(),
                body.getIssueDescription(),
                body.getEstimatedCost()
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
}