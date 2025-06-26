package com.example.betting_service.dto;

import lombok.Data;

@Data
public class BetUpdateRequest {
    private boolean cashout;
    private double cashoutAmount;
    private Double winAmount; // Nullable — only required if settling match
}
