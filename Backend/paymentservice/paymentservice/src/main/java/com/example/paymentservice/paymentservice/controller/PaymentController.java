package com.example.paymentservice.paymentservice.controller;

import com.example.paymentservice.paymentservice.model.BankDetails;
import com.example.paymentservice.paymentservice.service.PaymentService;
import com.razorpay.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/deposit")
    public ResponseEntity<?> createDeposit(@RequestBody Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            double amount = Double.parseDouble(request.get("amount").toString());

            Order order = paymentService.createDepositOrder(userId, amount);
            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Simulated confirmation after test payment is \"successful\"
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmDeposit(@RequestBody Map<String, String> request) {
        try {
            String orderId = request.get("orderId");
            paymentService.confirmDeposit(orderId);
            return ResponseEntity.ok(Map.of("status", "Payment confirmed and user balance updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request) {
        try {
            String userId = request.get("userId").toString();
            double amount = Double.parseDouble(request.get("amount").toString());

            Map<String, String> bank = (Map<String, String>) request.get("bankDetails");
            BankDetails bankDetails = BankDetails.builder()
                    .accountHolderName(bank.get("accountHolderName"))
                    .accountNumber(bank.get("accountNumber"))
                    .ifsc(bank.get("ifsc"))
                    .build();

            String msg = paymentService.withdraw(userId, amount, bankDetails);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

