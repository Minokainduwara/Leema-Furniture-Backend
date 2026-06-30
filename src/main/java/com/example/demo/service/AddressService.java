package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.BillingAddress;
import com.example.demo.entity.ShippingAddress;
import com.example.demo.entity.User;
import com.example.demo.repository.BillingAddressRepository;
import com.example.demo.repository.ShippingAddressRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShippingAddressRepository shippingRepo;

    @Autowired
    private BillingAddressRepository billingRepo;

    // ================= SHIPPING ADDRESSES =================

    public List<AddressResponse> getShippingAddresses(String email) {
        return shippingRepo.findByUser_Email(email).stream()
                .map(this::mapToShippingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addShippingAddress(String email, AddressRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ShippingAddress> existing = shippingRepo.findByUser_Email(email);
        boolean isDefault = req.getIsDefault() != null ? req.getIsDefault() : false;

        // If it's the first shipping address, force it to be default
        if (existing.isEmpty()) {
            isDefault = true;
        }

        if (isDefault) {
            // Unset current default
            for (ShippingAddress sa : existing) {
                if (Boolean.TRUE.equals(sa.getIsDefault())) {
                    sa.setIsDefault(false);
                    shippingRepo.save(sa);
                }
            }
        }

        ShippingAddress sa = ShippingAddress.builder()
                .user(user)
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .streetAddress(req.getStreetAddress())
                .apartmentSuite(req.getApartmentSuite())
                .city(req.getCity())
                .stateProvince(req.getStateProvince())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .isDefault(isDefault)
                .build();

        ShippingAddress saved = shippingRepo.save(sa);
        return mapToShippingResponse(saved);
    }

    @Transactional
    public AddressResponse updateShippingAddress(String email, Integer id, AddressRequest req) {
        ShippingAddress sa = shippingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));

        if (!sa.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied to shipping address");
        }

        boolean isDefault = req.getIsDefault() != null ? req.getIsDefault() : false;

        if (isDefault && !Boolean.TRUE.equals(sa.getIsDefault())) {
            // Unset others
            List<ShippingAddress> existing = shippingRepo.findByUser_Email(email);
            for (ShippingAddress other : existing) {
                if (!other.getId().equals(id) && Boolean.TRUE.equals(other.getIsDefault())) {
                    other.setIsDefault(false);
                    shippingRepo.save(other);
                }
            }
        }

        sa.setFullName(req.getFullName());
        sa.setPhoneNumber(req.getPhoneNumber());
        sa.setEmail(req.getEmail());
        sa.setStreetAddress(req.getStreetAddress());
        sa.setApartmentSuite(req.getApartmentSuite());
        sa.setCity(req.getCity());
        sa.setStateProvince(req.getStateProvince());
        sa.setPostalCode(req.getPostalCode());
        sa.setCountry(req.getCountry());
        sa.setIsDefault(isDefault);

        ShippingAddress updated = shippingRepo.save(sa);
        return mapToShippingResponse(updated);
    }

    @Transactional
    public void deleteShippingAddress(String email, Integer id) {
        ShippingAddress sa = shippingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));

        if (!sa.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied to shipping address");
        }

        boolean wasDefault = Boolean.TRUE.equals(sa.getIsDefault());
        shippingRepo.delete(sa);

        // If default was deleted, assign new default if any addresses remain
        if (wasDefault) {
            List<ShippingAddress> remaining = shippingRepo.findByUser_Email(email);
            if (!remaining.isEmpty()) {
                ShippingAddress newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                shippingRepo.save(newDefault);
            }
        }
    }

    private AddressResponse mapToShippingResponse(ShippingAddress sa) {
        return AddressResponse.builder()
                .id(sa.getId())
                .fullName(sa.getFullName())
                .phoneNumber(sa.getPhoneNumber())
                .email(sa.getEmail())
                .streetAddress(sa.getStreetAddress())
                .apartmentSuite(sa.getApartmentSuite())
                .city(sa.getCity())
                .stateProvince(sa.getStateProvince())
                .postalCode(sa.getPostalCode())
                .country(sa.getCountry())
                .isDefault(sa.getIsDefault())
                .build();
    }

    // ================= BILLING ADDRESSES =================

    public List<AddressResponse> getBillingAddresses(String email) {
        return billingRepo.findByUser_Email(email).stream()
                .map(this::mapToBillingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addBillingAddress(String email, AddressRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BillingAddress> existing = billingRepo.findByUser_Email(email);
        boolean isDefault = req.getIsDefault() != null ? req.getIsDefault() : false;

        // If it's the first billing address, force it to be default
        if (existing.isEmpty()) {
            isDefault = true;
        }

        if (isDefault) {
            // Unset current default
            for (BillingAddress ba : existing) {
                if (Boolean.TRUE.equals(ba.getIsDefault())) {
                    ba.setIsDefault(false);
                    billingRepo.save(ba);
                }
            }
        }

        BillingAddress ba = BillingAddress.builder()
                .user(user)
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber())
                .email(req.getEmail())
                .streetAddress(req.getStreetAddress())
                .apartmentSuite(req.getApartmentSuite())
                .city(req.getCity())
                .stateProvince(req.getStateProvince())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .isDefault(isDefault)
                .build();

        BillingAddress saved = billingRepo.save(ba);
        return mapToBillingResponse(saved);
    }

    @Transactional
    public AddressResponse updateBillingAddress(String email, Integer id, AddressRequest req) {
        BillingAddress ba = billingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing address not found"));

        if (!ba.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied to billing address");
        }

        boolean isDefault = req.getIsDefault() != null ? req.getIsDefault() : false;

        if (isDefault && !Boolean.TRUE.equals(ba.getIsDefault())) {
            // Unset others
            List<BillingAddress> existing = billingRepo.findByUser_Email(email);
            for (BillingAddress other : existing) {
                if (!other.getId().equals(id) && Boolean.TRUE.equals(other.getIsDefault())) {
                    other.setIsDefault(false);
                    billingRepo.save(other);
                }
            }
        }

        ba.setFullName(req.getFullName());
        ba.setPhoneNumber(req.getPhoneNumber());
        ba.setEmail(req.getEmail());
        ba.setStreetAddress(req.getStreetAddress());
        ba.setApartmentSuite(req.getApartmentSuite());
        ba.setCity(req.getCity());
        ba.setStateProvince(req.getStateProvince());
        ba.setPostalCode(req.getPostalCode());
        ba.setCountry(req.getCountry());
        ba.setIsDefault(isDefault);

        BillingAddress updated = billingRepo.save(ba);
        return mapToBillingResponse(updated);
    }

    @Transactional
    public void deleteBillingAddress(String email, Integer id) {
        BillingAddress ba = billingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing address not found"));

        if (!ba.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied to billing address");
        }

        boolean wasDefault = Boolean.TRUE.equals(ba.getIsDefault());
        billingRepo.delete(ba);

        // If default was deleted, assign new default if any addresses remain
        if (wasDefault) {
            List<BillingAddress> remaining = billingRepo.findByUser_Email(email);
            if (!remaining.isEmpty()) {
                BillingAddress newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                billingRepo.save(newDefault);
            }
        }
    }

    private AddressResponse mapToBillingResponse(BillingAddress ba) {
        return AddressResponse.builder()
                .id(ba.getId())
                .fullName(ba.getFullName())
                .phoneNumber(ba.getPhoneNumber())
                .email(ba.getEmail())
                .streetAddress(ba.getStreetAddress())
                .apartmentSuite(ba.getApartmentSuite())
                .city(ba.getCity())
                .stateProvince(ba.getStateProvince())
                .postalCode(ba.getPostalCode())
                .country(ba.getCountry())
                .isDefault(ba.getIsDefault())
                .build();
    }
}
