package com.example.paymentservice.service;

import com.example.paymentservice.client.WalletClient;
import com.example.paymentservice.dto.WalletCreditRequest;
import com.example.paymentservice.dto.WalletDeductRequest;
import com.example.paymentservice.model.BankDetails;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final WalletClient walletClient;

    public Order createDepositOrder(String userId, double amount) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // Razorpay expects amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + UUID.randomUUID());

        return razorpayClient.orders.create(orderRequest);
    }

    public String confirmDeposit(String userId, double amount, String orderId) {
        WalletCreditRequest creditRequest = new WalletCreditRequest();
        creditRequest.setAmount(amount);
        creditRequest.setDescription("Deposit Success for Order: " + orderId);
        creditRequest.setReferenceId("DEPOSIT-" + orderId);
        creditRequest.setType("DEPOSIT");

        return walletClient.creditAmount(userId, creditRequest);
    }

    public String withdraw(String userId, double amount, BankDetails bankDetails) {
        double currentBalance = walletClient.getUserBalance(userId);

        if (currentBalance < amount) {
            throw new RuntimeException("Insufficient balance in wallet for withdrawal.");
        }

        // ✅ Immediately deduct amount from wallet
        WalletDeductRequest deductRequest = new WalletDeductRequest();
        deductRequest.setAmount(amount);
        deductRequest.setDescription("Withdrawal initiated to bank account: " + bankDetails.getAccountNumber());
        deductRequest.setReferenceId("WITHDRAW-" + UUID.randomUUID());
        deductRequest.setType("WITHDRAWAL");

        String walletResponse = walletClient.deductAmount(userId, deductRequest);
        System.out.println("Wallet Deduct Response: " + walletResponse);

        // Proceed with sending money to bank / mark transaction pending etc
        return "Withdrawal of ₹" + amount + " initiated to account: " + bankDetails.getAccountNumber();
    }

}
