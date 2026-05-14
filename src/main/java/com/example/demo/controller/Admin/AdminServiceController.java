package com.example.demo.controller.Admin;

import com.example.demo.entity.Service;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminServiceController {

    private final AdminService adminService;

    @GetMapping
    public List<Service> getAll() {
        return adminService.getAllServices();
    }

    @GetMapping("/{id}")
    public Service getById(@PathVariable Integer id) {
        return adminService.getServiceById(id);
    }

    @PostMapping
    public Service create(@RequestBody Service service) {
        return adminService.createService(service);
    }

    @PutMapping("/{id}")
    public Service update(@PathVariable Integer id, @RequestBody Service service) {
        return adminService.updateService(id, service);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        adminService.deleteService(id);
        return "Service deleted successfully";
    }
}