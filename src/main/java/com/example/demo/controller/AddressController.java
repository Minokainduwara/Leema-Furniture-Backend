package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // SHIPPING

    @GetMapping("/shipping")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getShippingAddresses(
            @RequestParam Integer userId
    ) {
        List<AddressResponse> data = addressService.getShippingAddresses(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipping addresses fetched successfully", data));
    }

    @PostMapping("/shipping")
    public ResponseEntity<ApiResponse<AddressResponse>> createShippingAddress(
            @RequestParam Integer userId,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse data = addressService.createShippingAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Shipping address created", data));
    }

    @PutMapping("/shipping/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateShippingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse data = addressService.updateShippingAddress(userId, id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipping address updated", data));
    }

    @PatchMapping("/shipping/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultShippingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id
    ) {
        addressService.setDefaultShippingAddress(userId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Default shipping address updated", null));
    }

    @DeleteMapping("/shipping/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShippingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id
    ) {
        addressService.deleteShippingAddress(userId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipping address deleted", null));
    }

    // BILLING

    @GetMapping("/billing")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getBillingAddresses(
            @RequestParam Integer userId
    ) {
        List<AddressResponse> data = addressService.getBillingAddresses(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Billing addresses fetched successfully", data));
    }

    @PostMapping("/billing")
    public ResponseEntity<ApiResponse<AddressResponse>> createBillingAddress(
            @RequestParam Integer userId,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse data = addressService.createBillingAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Billing address created", data));
    }

    @PutMapping("/billing/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateBillingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse data = addressService.updateBillingAddress(userId, id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Billing address updated", data));
    }

    @PatchMapping("/billing/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultBillingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id
    ) {
        addressService.setDefaultBillingAddress(userId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Default billing address updated", null));
    }

    @DeleteMapping("/billing/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBillingAddress(
            @RequestParam Integer userId,
            @PathVariable Integer id
    ) {
        addressService.deleteBillingAddress(userId, id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Billing address deleted", null));
    }
}