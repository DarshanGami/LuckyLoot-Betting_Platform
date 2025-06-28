package com.example.betting_service.dto;

import lombok.Data;

@Data
public class WalletDeductRequest {
    private double amount;
    private String description;
    private String referenceId;
    private String type;  // ✅ Now just String, no enum
}
