package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.BillingAddress;
import com.example.demo.entity.ShippingAddress;
import com.example.demo.repository.BillingAddressRepository;
import com.example.demo.repository.ShippingAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final ShippingAddressRepository shippingAddressRepository;

    private final BillingAddressRepository billingAddressRepository;

    // =========================================================
    // SHIPPING
    // =========================================================

    public List<AddressResponse> getShippingAddresses(
            Integer userId
    ) {

        return shippingAddressRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapShippingResponse)
                .toList();
    }

    public AddressResponse getDefaultShippingAddress(
            Integer userId
    ) {

        ShippingAddress address =
                shippingAddressRepository
                        .findByUserIdAndIsDefaultTrue(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Default shipping address not found"
                                ));

        return mapShippingResponse(address);
    }

    public AddressResponse createShippingAddress(
            Integer userId,
            AddressRequest request
    ) {

        ShippingAddress address =
                ShippingAddress.builder()
                        .userId(userId)
                        .fullName(request.fullName())
                        .phoneNumber(request.phoneNumber())
                        .email(request.email())
                        .streetAddress(request.streetAddress())
                        .apartmentSuite(request.apartmentSuite())
                        .city(request.city())
                        .stateProvince(request.stateProvince())
                        .postalCode(request.postalCode())
                        .country(request.country())
                        .isDefault(
                                request.isDefault() != null
                                        ? request.isDefault()
                                        : false
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        ShippingAddress saved =
                shippingAddressRepository.save(address);

        return mapShippingResponse(saved);
    }

    public AddressResponse updateShippingAddress(
            Integer userId,
            Integer id,
            AddressRequest request
    ) {

        ShippingAddress address =
                shippingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipping address not found"
                                ));

        address.setFullName(request.fullName());
        address.setPhoneNumber(request.phoneNumber());
        address.setEmail(request.email());
        address.setStreetAddress(request.streetAddress());
        address.setApartmentSuite(request.apartmentSuite());
        address.setCity(request.city());
        address.setStateProvince(request.stateProvince());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        if (request.isDefault() != null) {
            address.setIsDefault(request.isDefault());
        }

        address.setUpdatedAt(LocalDateTime.now());

        ShippingAddress updated =
                shippingAddressRepository.save(address);

        return mapShippingResponse(updated);
    }

    public void setDefaultShippingAddress(
            Integer userId,
            Integer id
    ) {

        List<ShippingAddress> addresses =
                shippingAddressRepository.findByUserId(userId);

        for (ShippingAddress address : addresses) {

            address.setIsDefault(false);
        }

        ShippingAddress selected =
                shippingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipping address not found"
                                ));

        selected.setIsDefault(true);

        shippingAddressRepository.saveAll(addresses);

        shippingAddressRepository.save(selected);
    }

    public void deleteShippingAddress(
            Integer userId,
            Integer id
    ) {

        shippingAddressRepository
                .deleteByUserIdAndId(userId, id);
    }

    // =========================================================
    // BILLING
    // =========================================================

    public List<AddressResponse> getBillingAddresses(
            Integer userId
    ) {

        return billingAddressRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapBillingResponse)
                .toList();
    }

    public AddressResponse getDefaultBillingAddress(
            Integer userId
    ) {

        BillingAddress address =
                billingAddressRepository
                        .findByUserIdAndIsDefaultTrue(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Default billing address not found"
                                ));

        return mapBillingResponse(address);
    }

    public AddressResponse createBillingAddress(
            Integer userId,
            AddressRequest request
    ) {

        BillingAddress address =
                BillingAddress.builder()
                        .userId(userId)
                        .fullName(request.fullName())
                        .phoneNumber(request.phoneNumber())
                        .email(request.email())
                        .streetAddress(request.streetAddress())
                        .apartmentSuite(request.apartmentSuite())
                        .city(request.city())
                        .stateProvince(request.stateProvince())
                        .postalCode(request.postalCode())
                        .country(request.country())
                        .isDefault(
                                request.isDefault() != null
                                        ? request.isDefault()
                                        : false
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        BillingAddress saved =
                billingAddressRepository.save(address);

        return mapBillingResponse(saved);
    }

    public AddressResponse updateBillingAddress(
            Integer userId,
            Integer id,
            AddressRequest request
    ) {

        BillingAddress address =
                billingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Billing address not found"
                                ));

        address.setFullName(request.fullName());
        address.setPhoneNumber(request.phoneNumber());
        address.setEmail(request.email());
        address.setStreetAddress(request.streetAddress());
        address.setApartmentSuite(request.apartmentSuite());
        address.setCity(request.city());
        address.setStateProvince(request.stateProvince());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        if (request.isDefault() != null) {
            address.setIsDefault(request.isDefault());
        }

        address.setUpdatedAt(LocalDateTime.now());

        BillingAddress updated =
                billingAddressRepository.save(address);

        return mapBillingResponse(updated);
    }

    public void setDefaultBillingAddress(
            Integer userId,
            Integer id
    ) {

        List<BillingAddress> addresses =
                billingAddressRepository.findByUserId(userId);

        for (BillingAddress address : addresses) {

            address.setIsDefault(false);
        }

        BillingAddress selected =
                billingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Billing address not found"
                                ));

        selected.setIsDefault(true);

        billingAddressRepository.saveAll(addresses);

        billingAddressRepository.save(selected);
    }

    public void deleteBillingAddress(
            Integer userId,
            Integer id
    ) {

        billingAddressRepository
                .deleteByUserIdAndId(userId, id);
    }

    // =========================================================
    // RESPONSE MAPPERS
    // =========================================================

    private AddressResponse mapShippingResponse(
            ShippingAddress address
    ) {

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

    private AddressResponse mapBillingResponse(
            BillingAddress address
    ) {

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