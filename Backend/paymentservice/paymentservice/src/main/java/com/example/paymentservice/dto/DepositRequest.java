package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String userId;
    private double amount;
}
