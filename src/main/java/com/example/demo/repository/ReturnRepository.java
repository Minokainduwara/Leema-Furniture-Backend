package com.example.demo.repository;

import com.example.demo.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Integer> {
    List<Return> findByUserId(Integer userId);
}
