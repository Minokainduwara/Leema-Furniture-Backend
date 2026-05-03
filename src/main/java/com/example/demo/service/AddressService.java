package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.BillingAddress;
import com.example.demo.entity.ShippingAddress;
import com.example.demo.repository.BillingAddressRepository;
import com.example.demo.repository.ShippingAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    private final ShippingAddressRepository shippingRepo;
    private final BillingAddressRepository billingRepo;

    public AddressService(ShippingAddressRepository shippingRepo,
                          BillingAddressRepository billingRepo) {
        this.shippingRepo = shippingRepo;
        this.billingRepo = billingRepo;
    }

    // SHIPPING 

    public List<AddressResponse> getShippingAddresses(Integer userId) {
        return shippingRepo.findByUserId(userId.intValue())
                .stream()
                .map(this::mapShippingToResponse)
                .toList();
    }

    @Transactional
    public AddressResponse createShippingAddress(Integer userId, AddressRequest req) {

        ShippingAddress address = new ShippingAddress();

        address.setUserId(userId.intValue());
        address.setFullName(req.fullName());
        address.setPhoneNumber(req.phoneNumber());
        address.setEmail(req.email());
        address.setStreetAddress(req.streetAddress());
        address.setApartmentSuite(req.apartmentSuite());
        address.setCity(req.city());
        address.setStateProvince(req.stateProvince());
        address.setPostalCode(req.postalCode());
        address.setCountry(req.country());
        address.setIsDefault(req.isDefault() != null && req.isDefault());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        return mapShippingToResponse(shippingRepo.save(address));
    }

    @Transactional
    public AddressResponse updateShippingAddress(Integer userId, Integer id, AddressRequest req) {

        ShippingAddress address = shippingRepo.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));

        address.setFullName(req.fullName());
        address.setPhoneNumber(req.phoneNumber());
        address.setEmail(req.email());
        address.setStreetAddress(req.streetAddress());
        address.setApartmentSuite(req.apartmentSuite());
        address.setCity(req.city());
        address.setStateProvince(req.stateProvince());
        address.setPostalCode(req.postalCode());
        address.setCountry(req.country());
        address.setUpdatedAt(LocalDateTime.now());

        return mapShippingToResponse(shippingRepo.save(address));
    }

    @Transactional
    public void deleteShippingAddress(Integer userId, Integer id) {
        shippingRepo.deleteByUserIdAndId(userId.intValue(), id.intValue());
    }

    @Transactional
    public void setDefaultShippingAddress(Integer userId, Integer id) {

        List<ShippingAddress> list = shippingRepo.findByUserId(userId.intValue());

        for (ShippingAddress addr : list) {
            addr.setIsDefault(addr.getId().equals(id.intValue()));
        }

        shippingRepo.saveAll(list);
    }

    // BILLING 

    public List<AddressResponse> getBillingAddresses(Integer userId) {
        return billingRepo.findByUserId(userId.intValue())
                .stream()
                .map(this::mapBillingToResponse)
                .toList();
    }

    @Transactional
    public AddressResponse createBillingAddress(Integer userId, AddressRequest req) {

        BillingAddress address = new BillingAddress();

        address.setUserId(userId.intValue());
        address.setFullName(req.fullName());
        address.setPhoneNumber(req.phoneNumber());
        address.setEmail(req.email());
        address.setStreetAddress(req.streetAddress());
        address.setApartmentSuite(req.apartmentSuite());
        address.setCity(req.city());
        address.setStateProvince(req.stateProvince());
        address.setPostalCode(req.postalCode());
        address.setCountry(req.country());
        address.setIsDefault(req.isDefault() != null && req.isDefault());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        return mapBillingToResponse(billingRepo.save(address));
    }

    @Transactional
    public AddressResponse updateBillingAddress(Integer userId, Integer id, AddressRequest req) {

        BillingAddress address = billingRepo.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Billing address not found"));

        address.setFullName(req.fullName());
        address.setPhoneNumber(req.phoneNumber());
        address.setEmail(req.email());
        address.setStreetAddress(req.streetAddress());
        address.setApartmentSuite(req.apartmentSuite());
        address.setCity(req.city());
        address.setStateProvince(req.stateProvince());
        address.setPostalCode(req.postalCode());
        address.setCountry(req.country());
        address.setUpdatedAt(LocalDateTime.now());

        return mapBillingToResponse(billingRepo.save(address));
    }

    @Transactional
    public void setDefaultBillingAddress(Integer userId, Integer id) {

        List<BillingAddress> list = billingRepo.findByUserId(userId.intValue());

        for (BillingAddress addr : list) {
            addr.setIsDefault(addr.getId().equals(id.intValue()));
        }

        billingRepo.saveAll(list);
    }

    @Transactional
    public void deleteBillingAddress(Integer userId, Integer id) {
        billingRepo.deleteByUserIdAndId(userId.intValue(), id.intValue());
    }

    // MAPPERS 

    private AddressResponse mapShippingToResponse(ShippingAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getUserId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.getEmail(),
                address.getStreetAddress(),
                address.getApartmentSuite(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }

    private AddressResponse mapBillingToResponse(BillingAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getUserId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.getEmail(),
                address.getStreetAddress(),
                address.getApartmentSuite(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}