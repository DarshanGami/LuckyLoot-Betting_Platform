package com.example.wallet_service.model;

import com.example.wallet_service.enums.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    private String userId;

    private TransactionType type;

    private double amount;

    private double balanceAfter;

    private String description;

    private String referenceId;

    private Instant createdAt;
}
