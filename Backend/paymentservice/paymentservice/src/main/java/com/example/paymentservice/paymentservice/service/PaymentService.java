package com.example.paymentservice.paymentservice.service;

import com.example.paymentservice.paymentservice.model.*;
import com.example.paymentservice.paymentservice.repository.TransactionRepository;
import com.example.paymentservice.paymentservice.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public Order createDepositOrder(String userId, double amount) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new Exception("User not found");

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // Razorpay amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + UUID.randomUUID());

        Order order = razorpayClient.orders.create(orderRequest);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .razorpayOrderId(order.get("id"))
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(txn);

        return order;
    }

    public void confirmDeposit(String orderId) {
        Transaction txn = transactionRepository.findAll()
                .stream()
                .filter(t -> orderId.equals(t.getRazorpayOrderId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!txn.getStatus().equals("PENDING")) return;

        txn.setStatus("SUCCESS");
        transactionRepository.save(txn);

        User user = userRepository.findById(txn.getUserId()).orElseThrow();
        user.setTotalDeposit(user.getTotalDeposit() + txn.getAmount());
        user.getTransactions().add(txn.getId());
        userRepository.save(user);
    }

    public String withdraw(String userId, double amount, BankDetails bankDetails) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getTotalDeposit() < amount) {
            throw new Exception("Insufficient balance");
        }

        user.setTotalDeposit(user.getTotalDeposit() - amount);
        if (user.getBankDetails() == null) {
            user.setBankDetails(bankDetails);
        }
        userRepository.save(user);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .status("INITIATED")
                .timestamp(LocalDateTime.now())
                .razorpayOrderId(null)
                .build();
        transactionRepository.save(txn);

        return "Withdrawal request initiated (manual/simulated)";
    }
}
