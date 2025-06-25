package com.example.paymentservice.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    private String userId;
    private TransactionType type; // DEPOSIT or WITHDRAW
    private Double amount;
    private String status; // e.g., PENDING, SUCCESS, FAILED
    private String razorpayOrderId;
    private LocalDateTime timestamp;
}

