package com.example.paymentservice.dto;

import com.example.paymentservice.model.BankDetails;
import lombok.Data;

@Data
public class WithdrawRequest {
    private String userId;
    private double amount;
    private BankDetails bankDetails;
}
