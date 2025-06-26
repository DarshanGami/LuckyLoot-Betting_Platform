package com.example.betting_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "matches")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    private String id;

    private String team1;
    private String team2;

    private String score1;
    private String score2;
    private String status;

    private Boolean isBatting1;
    private Boolean isBatting2;

    private String odd1;
    private String odd2;

    private String time;          // ⬅️ NEW
    private String extraInfo;     // ⬅️ NEW
    private Boolean live;         // ⬅️ NEW

    private LocalDateTime createdAt;  // For TTL index
}
