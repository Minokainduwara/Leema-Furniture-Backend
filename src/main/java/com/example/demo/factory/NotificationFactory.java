package com.example.demo.factory;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;

public class NotificationFactory {

    public static Notification createOrderNotification(User user, String status, String orderNumber) {

        String title = "";
        String message = "";

        switch (status) {

            case "PENDING":
                title = "Order Placed";
                message = "Your order " + orderNumber + " has been placed successfully.";
                break;

            case "CONFIRMED":
                title = "Order Confirmed";
                message = "Your order " + orderNumber + " has been confirmed.";
                break;

            case "PROCESSING":
                title = "Order Processing";
                message = "Your order " + orderNumber + " is being prepared.";
                break;

            case "SHIPPED":
                title = "Order Shipped";
                message = "Your order " + orderNumber + " has been shipped.";
                break;

            case "DELIVERED":
                title = "Order Delivered";
                message = "Your order " + orderNumber + " has been delivered.";
                break;

            case "CANCELLED":
                title = "Order Cancelled";
                message = "Your order " + orderNumber + " has been cancelled.";
                break;

            case "REFUNDED":
                title = "Payment Refunded";
                message = "Refund completed for order " + orderNumber;
                break;

            case "RETURNED":
                title = "Order Returned";
                message = "Your returned order " + orderNumber + " is being processed.";
                break;
        }

        return Notification.builder()
                .user(user)
                .type("order")
                .title(title)
                .message(message)
                .isRead(false)
                .build();
    }
}