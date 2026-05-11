package com.example.demo.service;

import com.example.demo.dto.response.SellerDashboardResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SellerDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SellerDashboardResponse getDashboardData() {

        BigDecimal totalSales = orderRepository.getTotalSales();
        long orders = orderRepository.count();
        long products = productRepository.count();
        long categories = categoryRepository.count();

        return new SellerDashboardResponse(
                totalSales,
                orders,
                products,
                categories
        );
    }
}