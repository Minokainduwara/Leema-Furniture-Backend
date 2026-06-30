package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // ================= SHIPPING ADDRESSES =================

    @GetMapping("/shipping")
    public ResponseEntity<List<AddressResponse>> getShippingAddresses(Authentication auth) {
        return ResponseEntity.ok(addressService.getShippingAddresses(auth.getName()));
    }

    @PostMapping("/shipping")
    public ResponseEntity<AddressResponse> addShippingAddress(
            Authentication auth,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addShippingAddress(auth.getName(), request));
    }

    @PutMapping("/shipping/{id}")
    public ResponseEntity<AddressResponse> updateShippingAddress(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateShippingAddress(auth.getName(), id, request));
    }

    @DeleteMapping("/shipping/{id}")
    public ResponseEntity<Void> deleteShippingAddress(
            Authentication auth,
            @PathVariable Integer id) {
        addressService.deleteShippingAddress(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // ================= BILLING ADDRESSES =================

    @GetMapping("/billing")
    public ResponseEntity<List<AddressResponse>> getBillingAddresses(Authentication auth) {
        return ResponseEntity.ok(addressService.getBillingAddresses(auth.getName()));
    }

    @PostMapping("/billing")
    public ResponseEntity<AddressResponse> addBillingAddress(
            Authentication auth,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addBillingAddress(auth.getName(), request));
    }

    @PutMapping("/billing/{id}")
    public ResponseEntity<AddressResponse> updateBillingAddress(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateBillingAddress(auth.getName(), id, request));
    }

    @DeleteMapping("/billing/{id}")
    public ResponseEntity<Void> deleteBillingAddress(
            Authentication auth,
            @PathVariable Integer id) {
        addressService.deleteBillingAddress(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
