package com.example.wallet_service.dto;

import com.example.wallet_service.enums.TransactionType;
import lombok.Data;

@Data
public class DeductRequest {
    private double amount;
    private String description;
    private String referenceId;
    private TransactionType type; // e.g., BET_PLACED, WITHDRAWAL, etc.
}
