package com.example.demo.service;

import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShippingMethod;
import com.example.demo.repository.ShipmentRepository;
import com.example.demo.repository.ShippingMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShippingMethodRepository shippingMethodRepository;

    public Optional<Shipment> getShipmentByOrderId(Integer orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }

    public Optional<Shipment> getTrackingInfo(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId);
    }

    public List<ShippingMethod> getActiveShippingMethods() {
        return shippingMethodRepository.findByIsActiveTrue();
    }

    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    public Shipment updateShipment(Integer id, Shipment shipmentDetails) {
        return shipmentRepository.findById(id).map(s -> {
            s.setStatus(shipmentDetails.getStatus());
            s.setTrackingNumber(shipmentDetails.getTrackingNumber());
            s.setCarrier(shipmentDetails.getCarrier());
            return shipmentRepository.save(s);
        }).orElse(null);
    }
}
