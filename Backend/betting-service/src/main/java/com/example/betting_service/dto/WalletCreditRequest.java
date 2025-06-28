package com.example.betting_service.dto;

import lombok.Data;

@Data
public class WalletCreditRequest {

    private double amount;
    private String description;
    private String referenceId;
    private String type;  // e.g., "BET_WON" or "CASHOUT"

}
