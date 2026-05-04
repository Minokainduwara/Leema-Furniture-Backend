package com.example.demo.repository;

import com.example.demo.entity.UserActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Integer> {

    // Called in AdminUserService.getUserActivity()
    Page<UserActivityLog> findByUserId(Integer userId, Pageable pageable);
}

