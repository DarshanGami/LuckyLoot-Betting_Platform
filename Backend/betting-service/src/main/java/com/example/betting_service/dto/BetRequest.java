package com.example.betting_service.dto;

import lombok.Data;

@Data
public class BetRequest {
    private String matchId;
    private String team;
    private String oddAtBet;
    private double amount;
}
