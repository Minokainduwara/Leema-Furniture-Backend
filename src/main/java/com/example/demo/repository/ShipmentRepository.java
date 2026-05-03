package com.example.demo.repository;

import com.example.demo.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    Optional<Shipment> findByOrderId(Integer orderId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
