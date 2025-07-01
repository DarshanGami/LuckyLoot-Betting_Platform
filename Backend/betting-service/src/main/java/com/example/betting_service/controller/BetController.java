package com.example.betting_service.controller;

import com.example.betting_service.dto.BetRequest;
import com.example.betting_service.dto.SettleRequest;
import com.example.betting_service.model.Bet;
import com.example.betting_service.model.Match;
import com.example.betting_service.service.BetService;
import com.example.betting_service.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bet")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;
    private final MatchService matchService;

    // ✅ Place Bet - now accepts JSON body
    @PostMapping("/place")
    public ResponseEntity<Bet> placeBet(@RequestHeader("userId") String userId,
                                        @RequestBody BetRequest request) {
        Bet bet = betService.placeBet(userId, request.getMatchId(), request.getTeam(), request.getOddAtBet(), request.getAmount());
        return ResponseEntity.ok(bet);
    }

    // ✅ Fetch all bets of a user
    @GetMapping("/my-bets")
    public ResponseEntity<List<Bet>> getMyBets(@RequestHeader("userId") String userId) {
        return ResponseEntity.ok(betService.getBetsByUser(userId));
    }

    // ✅ Settle Bets for a match - now also takes JSON body
    @PostMapping("/settle")
    public ResponseEntity<String> settleMatch(@RequestBody SettleRequest request) {
        String result = betService.settleBetsForMatch(request.getMatchId(), request.getWinningTeam());
        return ResponseEntity.ok(result);
    }

    // ✅ Get all matches for frontend
    @GetMapping("/all-matches")
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @PostMapping("/cashout")
    public ResponseEntity<String> cashoutBet(@RequestHeader("userId") String userId,
                                             @RequestParam String betId) {
        String result = betService.cashoutBet(userId, betId);
        return ResponseEntity.ok(result);
    }


}
