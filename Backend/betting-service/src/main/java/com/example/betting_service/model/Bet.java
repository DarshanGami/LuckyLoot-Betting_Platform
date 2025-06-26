package com.example.betting_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bet {

    @Id
    private String id;

    private String userId;          // From JWT
    private String matchId;          // Match _id (team1_vs_team2)
    private String betId;            // Unique external ID like BET-xxxxxxx
    private String team;             // Team on which bet was placed
    private String oddAtBet;         // Odds at time of placing
    private double amount;           // Bet amount

    private boolean cashout;         // Whether user has cashed out
    private double cashoutAmount;    // Amount cashed out
    private double winAmount;        // Total win amount after settlement

    private boolean isSettled;       // ✅ New field - whether this bet is settled
    private boolean won;             // ✅ New field - did this bet win or not (after settlement)

    private LocalDateTime placedAt;  // When bet was placed
    private LocalDateTime cashoutAt; // When cashout happened (nullable)
}
