package com.example.paymentservice.paymentservice.controller;

import com.example.paymentservice.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam double amount) {
        try {
            String order = paymentService.createOrder(amount);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Order creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam String userId,
            @RequestParam double amount) {

        boolean updated = paymentService.handlePaymentSuccess(userId, amount);
        if (updated) {
            return ResponseEntity.ok("Wallet updated successfully.");
        } else {
            return ResponseEntity.status(404).body("User not found or update failed.");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawToBank(
            @RequestParam String userId,
            @RequestParam double amount) {

        try {
            boolean success = paymentService.withdrawToBank(userId, amount);
            if (success) {
                return ResponseEntity.ok("Withdrawal successful.");
            } else {
                return ResponseEntity.status(400).body("Withdrawal failed. Check balance or user info.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Withdrawal error: " + e.getMessage());
        }
    }
}
