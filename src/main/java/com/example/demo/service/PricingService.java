package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.enums.DiscountType;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ProductDiscountRepository productDiscountRepository;
    private final CategoryDiscountRepository categoryDiscountRepository;

    public BigDecimal calculateFinalPrice(Product product) {

        BigDecimal price = product.getPrice();


        ProductDiscount pd = productDiscountRepository
                .findActiveDiscount(product.getId(), LocalDateTime.now());

        if (pd != null) {
            return applyDiscount(price, pd.getDiscountType(), pd.getValue());
        }


        CategoryDiscount cd = categoryDiscountRepository
                .findActiveDiscount(product.getCategory().getId(), LocalDateTime.now());

        if (cd != null) {
            return applyDiscount(price, cd.getDiscountType(), cd.getValue());
        }

        return price;
    }

    private BigDecimal applyDiscount(BigDecimal price,
                                     DiscountType type,
                                     BigDecimal value) {

        if (type == DiscountType.PERCENTAGE) {
            return price.subtract(
                    price.multiply(value).divide(BigDecimal.valueOf(100))
            );
        }

        return price.subtract(value);
    }
}