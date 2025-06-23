package com.example.wallet_service.controller;

import com.example.wallet_service.dto.CreditRequest;
import com.example.wallet_service.dto.DeductRequest;
import com.example.wallet_service.model.Transaction;
import com.example.wallet_service.model.Wallet;
import com.example.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Create a wallet for the authenticated user
    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestHeader("userId") String userId) {
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    // Get wallet balance
    @GetMapping("/balance")
    public ResponseEntity<Wallet> getBalance(@RequestHeader("userId") String userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    // Credit funds to wallet (e.g., deposit, winnings)
    @PostMapping("/credit")
    public ResponseEntity<Wallet> creditWallet(
            @RequestHeader("userId") String userId,
            @RequestBody CreditRequest request) {
        return ResponseEntity.ok(walletService.credit(userId, request));
    }

    // Deduct funds from wallet (e.g., bet placed)
    @PostMapping("/deduct")
    public ResponseEntity<Wallet> deductWallet(
            @RequestHeader("userId") String userId,
            @RequestBody DeductRequest request) {
        return ResponseEntity.ok(walletService.deduct(userId, request));
    }

    // Get transaction history
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestHeader("userId") String userId) {
        return ResponseEntity.ok(walletService.getAllTransactions(userId));
    }
}
