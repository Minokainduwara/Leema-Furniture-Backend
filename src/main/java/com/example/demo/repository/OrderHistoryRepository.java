package com.example.demo.repository;

import com.example.demo.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer> {

    List<OrderHistory> findByOrder_IdOrderByCreatedAtDesc(Integer orderId);

    List<OrderHistory> findByOrder_User_IdOrderByCreatedAtDesc(Integer userId);
    List<OrderHistory> findByOrder_User_Id(Integer userId);

}