package com.example.betting_service.dto;

import lombok.Data;

@Data
public class SettleRequest {
    private String matchId;
    private String winningTeam;
}
