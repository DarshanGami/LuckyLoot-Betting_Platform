package com.example.betting_service.repository;

import com.example.betting_service.model.Bet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BetRepository extends MongoRepository<Bet, String> {
    List<Bet> findByUserId(String userId);
    List<Bet> findByMatchId(String matchId);
    Optional<Bet> findByBetId(String betId);
}
