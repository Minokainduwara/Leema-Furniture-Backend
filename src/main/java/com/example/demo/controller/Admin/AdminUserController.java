package com.example.demo.controller.Admin;

import com.example.demo.dto.request.AdminUpdateUserStatusRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getAllUsers(role, status, search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUserById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id,
                                              @Valid @RequestBody AdminUpdateUserStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.updateUserStatus(id, request)));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id,
                                            @RequestParam String role) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.updateUserRole(id, role)));
    }

    @PatchMapping("/{id}/seller-details")
    public ResponseEntity<?> updateSellerDetails(
            @PathVariable Integer id,
            @RequestParam(required = false) String nicNumber,
            @RequestParam(required = false) String sellerAddress,
            @RequestParam(required = false) String nicImage) {
        return ResponseEntity.ok(ApiResponse.success(
            adminUserService.updateSellerDetails(id, nicNumber, sellerAddress, nicImage)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Integer id,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminUserService.getUserActivity(id, pageable)));
    }
}