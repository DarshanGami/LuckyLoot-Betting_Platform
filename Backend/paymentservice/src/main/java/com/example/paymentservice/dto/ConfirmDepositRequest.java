package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class ConfirmDepositRequest {
    private double amount;
    private String orderId;
}
