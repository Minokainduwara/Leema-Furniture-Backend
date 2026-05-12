package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {

        private int totalOrders;
        private int pendingOrders;
        private int wishlistCount;
        private int serviceCount;

}
