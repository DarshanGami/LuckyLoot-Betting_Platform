package com.example.paymentservice.controller;

import com.example.paymentservice.dto.ConfirmDepositRequest;
import com.example.paymentservice.dto.DepositRequest;
import com.example.paymentservice.dto.WithdrawRequest;
import com.example.paymentservice.model.BankDetails;
import com.example.paymentservice.service.PaymentService;
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
    public ResponseEntity<?> createDeposit(@RequestBody DepositRequest request) {
        try {
            Order order = paymentService.createDepositOrder(request.getUserId(), request.getAmount());
            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmDeposit(@RequestBody ConfirmDepositRequest request) {
        try {
            String walletResponse = paymentService.confirmDeposit(
                    request.getUserId(), request.getAmount(), request.getOrderId()
            );
            return ResponseEntity.ok(Map.of("status", "Payment confirmed", "walletResponse", walletResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request) {
        try {
            String msg = paymentService.withdraw(
                    request.getUserId(),
                    request.getAmount(),
                    request.getBankDetails()
            );
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
