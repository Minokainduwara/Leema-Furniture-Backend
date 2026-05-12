package com.example.demo.service;

import com.example.demo.dto.response.UserDashboardResponse;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.WishlistRepository;
import com.example.demo.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDashboardService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private WishlistRepository wishlistRepo;
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public UserDashboardResponse getOverview(String email) {

        int totalOrders = orderRepo.countByUser_Email(email);

        int pendingOrders =
                orderRepo.countByUser_EmailAndStatus(email, Order.OrderStatus.PENDING);

        int wishlistCount =
                wishlistRepo.countByUser_Email(email);
        int serviceCount=serviceRequestRepository.countByUser_Email(email);

        return new UserDashboardResponse(
                totalOrders,
                pendingOrders,
                wishlistCount,
                serviceCount
        );
    }
}
