package com.example.demo.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderResponse {

        private Integer id;
        private String orderNumber;
        private String status;
        private BigDecimal totalAmount;

}
