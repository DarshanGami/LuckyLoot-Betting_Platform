package com.example.betting_service.kafka;

import com.example.betting_service.model.Match;
import com.example.betting_service.service.MatchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OddsConsumer {

    private final MatchService matchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "cricket-odds-update", groupId = "betting-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String value = record.value();
            JsonNode node = objectMapper.readTree(value);

            String team1 = node.has("team1") ? node.get("team1").asText() : null;
            String team2 = node.has("team2") ? node.get("team2").asText() : null;
            String id = team1 + "_vs_" + team2;

            // ✅ Handle match deletion
            if (node.has("deleted") && node.get("deleted").asBoolean()) {
                matchService.deleteMatchById(id);
                System.out.println("❌ Deleted match from DB: " + id);
                return;
            }

            // ✅ Build Match object
            Match match = Match.builder()
                    .id(id)
                    .team1(team1)
                    .team2(team2)
                    .score1(node.has("score1") && !node.get("score1").isNull() ? node.get("score1").asText() : null)
                    .score2(node.has("score2") && !node.get("score2").isNull() ? node.get("score2").asText() : null)
                    .status(node.has("status") && !node.get("status").isNull() ? node.get("status").asText() : null)
                    .isBatting1(node.has("isBatting1") && !node.get("isBatting1").isNull() ? node.get("isBatting1").asBoolean() : null)
                    .isBatting2(node.has("isBatting2") && !node.get("isBatting2").isNull() ? node.get("isBatting2").asBoolean() : null)
                    .odd1(node.has("odd1") && !node.get("odd1").isNull() ? node.get("odd1").asText() : null)
                    .odd2(node.has("odd2") && !node.get("odd2").isNull() ? node.get("odd2").asText() : null)
                    .time(node.has("time") && !node.get("time").isNull() ? node.get("time").asText() : null)
                    .extraInfo(node.has("extraInfo") && !node.get("extraInfo").isNull() ? node.get("extraInfo").asText() : null)
                    .live(node.has("live") && !node.get("live").isNull() ? node.get("live").asBoolean() : null)
                    .createdAt(LocalDateTime.now())  // ✅ Important for TTL indexing
                    .build();

            // ✅ Save via Service (so TTL logic stays consistent)
            matchService.saveOrUpdateMatch(match);
            System.out.println("✅ Saved/Updated match: " + id);

        } catch (Exception e) {
            System.err.println("⚠️ Kafka Consumer Error: " + e.getMessage());
        }
    }
}
