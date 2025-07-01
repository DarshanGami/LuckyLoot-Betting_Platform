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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public double getBalance(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found for user: " + userId));

        System.out.println(wallet.getBalance());
        return wallet.getBalance();
    }

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


    public Wallet getWallet(String userId) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isPresent()) {
            return walletOpt.get();
        }
        return createWallet(userId);
    }




    public Wallet credit(String userId, CreditRequest request) {
        Wallet wallet = getWallet(userId);

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
        Wallet wallet = getWallet(userId);

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
