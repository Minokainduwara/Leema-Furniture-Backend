package com.example.demo.repository;

import com.example.demo.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Integer> {

    List<Repair> findByUserId(Integer userId);

    List<Repair> findByHandledById(Integer sellerId);
}