package com.example.demo.service;

import com.example.demo.entity.Service;
import com.example.demo.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AdminService {

    private final ServiceRepository serviceRepository;

    // ─────────────────────────────────────────────
    // GET ALL SERVICES
    // ─────────────────────────────────────────────
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // ─────────────────────────────────────────────
    // GET SERVICE BY ID
    // ─────────────────────────────────────────────
    public Service getServiceById(Integer id) {
        return findService(id);
    }

    // ─────────────────────────────────────────────
    // CREATE SERVICE
    // ─────────────────────────────────────────────
    @Transactional
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    // ─────────────────────────────────────────────
    // UPDATE SERVICE
    // ─────────────────────────────────────────────
    @Transactional
    public Service updateService(Integer id, Service request) {

        Service service = findService(id);

        service.setName(request.getName());
        service.setType(request.getType());
        service.setPrice(request.getPrice());
        service.setDescription(request.getDescription());
        service.setLongDescription(request.getLongDescription());
        service.setIcon(request.getIcon());
        service.setImage(request.getImage());
        service.setActive(request.getActive());
        service.setDisplayOrder(request.getDisplayOrder());

        return serviceRepository.save(service);
    }

    // ─────────────────────────────────────────────
    // DELETE SERVICE (SOFT DELETE OPTIONAL)
    // ─────────────────────────────────────────────
    @Transactional
    public void deleteService(Integer id) {

        Service service = findService(id);

        serviceRepository.delete(service);
    }

    // ─────────────────────────────────────────────
    // FIND SERVICE (SAFE)
    // ─────────────────────────────────────────────
    private Service findService(Integer id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found: " + id));
    }
}