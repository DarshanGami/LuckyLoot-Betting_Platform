package com.example.paymentservice.paymentservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetails {
    private String accountNumber;
    private String ifsc;
    private String accountHolderName;
}
