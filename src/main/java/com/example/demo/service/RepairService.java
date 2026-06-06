package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // ✅ CREATE REPAIR (CORRECT LOGIC)
    public Repair createRepair(
            Integer userId,
            Integer productId,
            Integer orderId,
            String issueDescription,
            Double estimatedCost
    ) {

        // 1. Get order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. Get user properly (IMPORTANT FIX)
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Product (optional)
        Product product = null;
        if (productId != null) {
            product = productRepository.findById(productId)
                    .orElse(null);
        }

        // 4. Create repair
        Repair repair = Repair.builder()
                .user(user)
                .order(order)
                .product(product)
                .issueDescription(issueDescription)
                .estimatedCost(estimatedCost)
                .status(Repair.RepairStatus.REQUESTED)
                .build();

        return repairRepository.save(repair);
    }

    // ✅ GET ALL
    public List<Repair> getAll() {
        return repairRepository.findAll();
    }

    public List<Repair> getRepairsByOrderUser(Integer orderId) {

        // 🔥 1. Get Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 2. Get User from Order
        User user = order.getUser();

        if (user == null) {
            throw new RuntimeException("User not found for this order");
        }

        // 🔥 3. Get all repairs of that user
        return repairRepository.findByUserId(user.getId());
    }
    // ✅ GET BY USER
    public List<Repair> getByUser(Integer userId) {
        return repairRepository.findByUserId(userId);
    }

    // ✅ GET BY ORDER
    public List<Repair> getByOrder(Integer orderId) {
        return repairRepository.findByOrderId(orderId);
    }

    // ✅ GET BY PRODUCT
    public List<Repair> getByProduct(Integer productId) {
        return repairRepository.findByProductId(productId);
    }

    // ✅ GET BY STATUS
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