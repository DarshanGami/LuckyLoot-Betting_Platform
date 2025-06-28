package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class WalletDeductRequest {
    private double amount;
    private String description;
    private String referenceId;
    private String type;  // Example: "WITHDRAWAL"
}
