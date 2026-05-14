package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairService {

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ✅ CREATE REPAIR REQUEST
    public Repair createRepair(
            Integer userId,
            Integer productId,
            Integer orderId,
            String issueDescription,
            Double estimatedCost
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = null;
        Order order = null;

        if (productId != null) {
            product = productRepository.findById(productId)
                    .orElse(null);
        }

        if (orderId != null) {
            order = orderRepository.findById(orderId)
                    .orElse(null);
        }

        Repair repair = Repair.builder()
                .user(user)
                .product(product)
                .order(order)
                .issueDescription(issueDescription)   // ✅ FIXED
                .estimatedCost(estimatedCost)
                .status(Repair.RepairStatus.REQUESTED)
                .build();

        return repairRepository.save(repair);
    }

    // ✅ GET ALL
    public List<Repair> getAll() {
        return repairRepository.findAll();
    }

    // ✅ FILTERS
    public List<Repair> getByUser(Integer userId) {
        return repairRepository.findByUserId(userId);
    }

    public List<Repair> getByOrder(Integer orderId) {
        return repairRepository.findByOrderId(orderId);
    }

    public List<Repair> getByProduct(Integer productId) {
        return repairRepository.findByProductId(productId);
    }

    public List<Repair> getByStatus(String status) {
        return repairRepository.findByStatus(
                Repair.RepairStatus.valueOf(status.toUpperCase())
        );
    }

    // ✅ UPDATE STATUS
    public Repair updateStatus(Integer id, String status) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        repair.setStatus(Repair.RepairStatus.valueOf(status.toUpperCase()));

        return repairRepository.save(repair);
    }
}