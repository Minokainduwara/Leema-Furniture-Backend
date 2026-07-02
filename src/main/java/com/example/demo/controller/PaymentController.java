package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;

    @Value("${payhere.sandbox:true}")
    private boolean sandbox;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    @Value("${payhere.return-url}")
    private String returnUrl;

    @Value("${payhere.cancel-url}")
    private String cancelUrl;

    @Value("${payhere.notify-url}")
    private String notifyUrl;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Initiates a PayHere payment. Returns the merchant info + signed hash that
     * the frontend submits to PayHere's hosted checkout page.
     *
     * PayHere hash formula (uppercase MD5):
     *   hash = md5(merchant_id + order_id + amount + currency + md5(merchant_secret).toUpperCase()).toUpperCase()
     */
    @PostMapping("/payhere/initiate")
    public ResponseEntity<?> initiate(@RequestBody Map<String, Object> body) {
        try {
            String orderId = String.valueOf(body.get("orderId"));
            String currency = String.valueOf(body.getOrDefault("currency", "LKR"));

            // Fetch order securely to get exact amount rather than trusting frontend
            Order order = orderService.getOrderById(Integer.parseInt(orderId));

            // Format amount exactly the way PayHere expects: two decimal places
            BigDecimal amountBd = order.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
            String amount = amountBd.toPlainString();

            String hashedSecret = md5(merchantSecret).toUpperCase();
            String hashInput = merchantId + orderId + amount + currency + hashedSecret;
            String hash = md5(hashInput).toUpperCase();

            System.out.println("=== PAYHERE HASH DEBUG ===");
            System.out.println("merchantId: [" + merchantId + "]");
            System.out.println("orderId: [" + orderId + "]");
            System.out.println("amount: [" + amount + "]");
            System.out.println("currency: [" + currency + "]");
            System.out.println("merchantSecret: [" + merchantSecret + "]");
            System.out.println("hashedSecret: [" + hashedSecret + "]");
            System.out.println("hashInput: [" + hashInput + "]");
            System.out.println("hash: [" + hash + "]");
            System.out.println("==========================");

            Map<String, Object> response = new HashMap<>();
            response.put("sandbox", sandbox);
            response.put("merchantId", merchantId);
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("currency", currency);
            response.put("hash", hash);
            response.put("returnUrl", returnUrl);
            response.put("cancelUrl", cancelUrl);
            response.put("notifyUrl", notifyUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to build PayHere payload: " + e.getMessage());
        }
    }

    /**
     * Server-to-server callback from PayHere. Validates the signature and
     * marks the order as COMPLETED / FAILED based on status_code.
     *
     * Note: This URL must be publicly reachable (use ngrok or similar in dev).
     */
    @PostMapping("/payhere/notify")
    public ResponseEntity<?> notifyPayHere(@RequestParam Map<String, String> params) {
        try {
            String merchantIdParam = params.get("merchant_id");
            String orderId = params.get("order_id");
            String payhereAmount = params.get("payhere_amount");
            String payhereCurrency = params.get("payhere_currency");
            String statusCode = params.get("status_code");
            String md5sig = params.get("md5sig");

            if (orderId == null || statusCode == null || md5sig == null) {
                return ResponseEntity.badRequest().body("Missing required PayHere params");
            }

            String hashedSecret = md5(merchantSecret).toUpperCase();
            String expected = md5(merchantIdParam + orderId + payhereAmount + payhereCurrency + statusCode + hashedSecret).toUpperCase();

            if (!expected.equals(md5sig)) {
                return ResponseEntity.status(400).body("Invalid signature");
            }

            // status_code: 2 = success, 0 = pending, -1 = cancelled, -2 = failed, -3 = chargedback
            Integer orderIdInt;
            try {
                orderIdInt = Integer.parseInt(orderId);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body("Invalid orderId");
            }

            String newStatus;
            switch (statusCode) {
                case "2":  newStatus = "COMPLETED"; break;
                case "0":  newStatus = "PENDING";   break;
                case "-1": newStatus = "CANCELLED"; break;
                case "-2": newStatus = "FAILED";    break;
                case "-3": newStatus = "REFUNDED";  break;
                default:   newStatus = "PENDING";
            }

            Order updated = orderService.updatePaymentStatus(orderIdInt, newStatus);
            return ResponseEntity.ok(Map.of("orderId", updated.getId(), "paymentStatus", newStatus));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Notify handler error: " + e.getMessage());
        }
    }

    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
