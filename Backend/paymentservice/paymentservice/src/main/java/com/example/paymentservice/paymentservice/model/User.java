package com.example.paymentservice.paymentservice.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    private Double totalDeposit = 0.0;

    private BankDetails bankDetails;

    private List<String> transactions; // List of transaction IDs
}
