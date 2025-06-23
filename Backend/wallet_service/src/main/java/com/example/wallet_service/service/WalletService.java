package com.example.wallet_service.service;

import com.example.wallet_service.dto.CreditRequest;
import com.example.wallet_service.dto.DeductRequest;
import com.example.wallet_service.enums.TransactionType;
import com.example.wallet_service.exception.InsufficientBalanceException;
import com.example.wallet_service.model.Transaction;
import com.example.wallet_service.model.Wallet;
import com.example.wallet_service.repository.TransactionRepository;
import com.example.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public Wallet createWallet(String userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Wallet already exists for user");
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(0.0)
                .build();

        return walletRepository.save(wallet);
    }

    public Wallet getBalance(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Wallet credit(String userId, CreditRequest request) {
        Wallet wallet = getBalance(userId);

        double newBalance = wallet.getBalance() + request.getAmount();
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .amount(request.getAmount())
                .type(request.getType())
                .balanceAfter(newBalance)
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(txn);

        return wallet;
    }

    public Wallet deduct(String userId, DeductRequest request) {
        Wallet wallet = getBalance(userId);

        if (wallet.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient funds");
        }

        double newBalance = wallet.getBalance() - request.getAmount();
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .amount(request.getAmount())
                .type(request.getType())
                .balanceAfter(newBalance)
                .description(request.getDescription())
                .referenceId(request.getReferenceId())
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(txn);

        return wallet;
    }

    public List<Transaction> getAllTransactions(String userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
