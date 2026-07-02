package com.example.demo.repository;

import com.example.demo.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Integer> {

    // get repairs by user
    List<Repair> findByUserId(Integer userId);

    // get repairs by order
    List<Repair> findByOrderId(Integer orderId);

    // get repairs by product
    List<Repair> findByProductId(Integer productId);

    // filter by status
    List<Repair> findByStatus(Repair.RepairStatus status);

    List<Repair> findByHandledBy_Id(Integer staffId);

}