package com.example.demo.repository;

import com.example.demo.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Integer> {

    Page<AdminLog> findByAdminId(Integer adminId, Pageable pageable);

    @Query("SELECT al FROM AdminLog al WHERE " +
            "(:adminId IS NULL OR al.admin.id = :adminId) AND " +
            "(:entityType IS NULL OR al.entityType = :entityType) AND " +
            "(:action IS NULL OR al.action = :action)")
    Page<AdminLog> findWithFilters(@Param("adminId") Integer adminId,
                                   @Param("entityType") String entityType,
                                   @Param("action") String action,
                                   Pageable pageable);
}