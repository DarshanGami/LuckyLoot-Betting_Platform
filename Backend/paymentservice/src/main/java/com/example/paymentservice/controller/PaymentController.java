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
    public ResponseEntity<?> confirmDeposit(@RequestHeader("userId") String userId,@RequestBody ConfirmDepositRequest request) {
        try {
            String walletResponse = paymentService.confirmDeposit(
                    userId, request.getAmount(), request.getOrderId()
            );
            return ResponseEntity.ok(Map.of("status", "Payment confirmed", "walletResponse", walletResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader("userId") String userId,
                                      @RequestBody WithdrawRequest request) {
        System.out.println("✅ UserId from header: " + userId);
        System.out.println("✅ Withdraw request: " + request);

        try {
            String msg = paymentService.withdraw(
                    userId,
                    request.getAmount(),
                    request.getBankDetails()
            );
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
