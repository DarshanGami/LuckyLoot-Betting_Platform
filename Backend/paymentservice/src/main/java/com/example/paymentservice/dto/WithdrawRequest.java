package com.example.paymentservice.dto;

import com.example.paymentservice.model.BankDetails;
import lombok.Data;

@Data
public class WithdrawRequest {
    private double amount;
    private BankDetails bankDetails;
}
