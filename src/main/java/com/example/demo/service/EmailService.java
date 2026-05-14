package com.example.demo.service;

import com.example.demo.entity.Order;
import lombok.RequiredArgsConstructor;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ============================================
    // ORDER CONFIRMATION EMAIL
    // ============================================

    @Async
    public void sendOrderConfirmationEmail(
        Order order
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(
                    order.getUser().getEmail()
            );

            helper.setSubject(
                    "Order Confirmation - "
                            + order.getOrderNumber()
            );

            String html = """
                    <div style="
                        font-family: Arial, sans-serif;
                        max-width: 600px;
                        margin: auto;
                        padding: 20px;
                        background: #f9f9f9;
                    ">

                        <div style="
                            background: #111827;
                            color: white;
                            padding: 20px;
                            border-radius: 12px 12px 0 0;
                        ">
                            <h1>
                                Leema Furniture
                            </h1>

                            <p>
                                Order Confirmation
                            </p>
                        </div>

                        <div style="
                            background: white;
                            padding: 25px;
                            border-radius: 0 0 12px 12px;
                        ">

                            <h2>
                                Hello %s,
                            </h2>

                            <p>
                                Thank you for your order.
                            </p>

                            <hr>

                            <p>
                                <strong>Order Number:</strong>
                                %s
                            </p>

                            <p>
                                <strong>Total Amount:</strong>
                                LKR %s
                            </p>

                            <p>
                                <strong>Payment Method:</strong>
                                %s
                            </p>

                            <p>
                                <strong>Payment Status:</strong>
                                %s
                            </p>

                            <div style="margin-top: 30px;">

                                <a href="http://localhost:5173/user/orders"
                                style="
                                    background: #f59e0b;
                                    color: white;
                                    padding: 12px 20px;
                                    border-radius: 8px;
                                    text-decoration: none;
                                    font-weight: bold;
                                ">
                                    View Orders
                                </a>

                            </div>

                            <p style="
                                margin-top: 40px;
                                color: #666;
                                font-size: 14px;
                            ">
                                Thank you for shopping with us.
                            </p>

                        </div>

                    </div>
                    """.formatted(
                    order.getShippingAddress()
                            .getFullName(),

                    order.getOrderNumber(),

                    order.getTotalAmount(),

                    order.getPaymentMethod(),

                    order.getPaymentStatus()
            );

            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException ex) {

            throw new RuntimeException(
                    "Failed to send email"
            );
        }
    }
}