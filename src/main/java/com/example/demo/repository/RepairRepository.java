package com.example.demo.repository;

import com.example.demo.entity.Repair;
import com.example.demo.enums.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Integer> {

    List<Repair> findByUserId(Integer userId);

    List<Repair> findByStatus(RepairStatus status);
    List<Repair> findByHandledById(Integer handledById);
}