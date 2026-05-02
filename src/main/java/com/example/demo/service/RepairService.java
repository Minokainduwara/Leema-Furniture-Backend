package com.example.demo.service;

import com.example.demo.entity.Repair;
import com.example.demo.entity.User;
import com.example.demo.enums.RepairStatus;
import com.example.demo.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairService {

    @Autowired
    private RepairRepository repairRepository;

    // CREATE REPAIR (customer)
    public Repair createRepair(Repair repair) {
        repair.setStatus(RepairStatus.REQUESTED);
        return repairRepository.save(repair);
    }

    // ALL (admin only)
    public List<Repair> getAllRepairs() {
        return repairRepository.findAll();
    }

    // CUSTOMER - MY REPAIRS
    public List<Repair> getRepairsByUser(Integer userId) {
        return repairRepository.findByUserId(userId);
    }

    // SELLER - ASSIGNED REPAIRS
    public List<Repair> getRepairsBySeller(Integer sellerId) {
        return repairRepository.findByHandledById(sellerId);
    }

    // ASSIGN REPAIR TO SELLER
    public Repair assignRepair(Integer repairId, User seller) {

        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        repair.setHandledBy(seller);
        repair.setStatus(RepairStatus.IN_PROGRESS);

        return repairRepository.save(repair);
    }

    // UPDATE STATUS
    public Repair updateStatus(Integer repairId, RepairStatus status) {

        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        repair.setStatus(status);

        return repairRepository.save(repair);
    }
}