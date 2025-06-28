package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class ConfirmDepositRequest {
    private String userId;
    private double amount;
    private String orderId;
}
