package com.example.demo.controller;

import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShippingMethod;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Shipment> getShipmentByOrderId(@PathVariable Integer orderId) {
        return shipmentService.getShipmentByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<Shipment> getTrackingInfo(@PathVariable Integer id) {
        return shipmentService.getTrackingInfo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/methods")
    public ResponseEntity<List<ShippingMethod>> getShippingMethods() {
        return ResponseEntity.ok(shipmentService.getActiveShippingMethods());
    }

    @PostMapping("/admin")
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.ok(shipmentService.createShipment(shipment));
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Integer id, @RequestBody Shipment shipment) {
        Shipment updated = shipmentService.updateShipment(id, shipment);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
