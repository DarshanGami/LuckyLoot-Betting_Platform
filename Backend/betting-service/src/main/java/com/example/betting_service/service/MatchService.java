package com.example.betting_service.service;

import com.example.betting_service.model.Match;
import com.example.betting_service.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    // ✅ Get match by ID
    public Optional<Match> getMatchById(String matchId) {
        return matchRepository.findById(matchId);
    }

    // ✅ Get all matches (for frontend)
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    // ✅ Save or update match (used by Kafka consumer)
    public void saveOrUpdateMatch(Match match) {
        match.setCreatedAt(LocalDateTime.now());  // Important for TTL refresh
        matchRepository.save(match);
    }

    // ✅ Delete match by ID (used when receiving "deleted" from Kafka)
    public void deleteMatchById(String matchId) {
        matchRepository.deleteById(matchId);
    }
}
