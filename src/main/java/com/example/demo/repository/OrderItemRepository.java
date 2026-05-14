package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{



    List<OrderItem> findByOrder(Order order);




}
