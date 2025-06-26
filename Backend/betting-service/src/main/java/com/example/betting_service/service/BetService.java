package com.example.betting_service.service;

import com.example.betting_service.model.Bet;
import com.example.betting_service.model.Match;
import com.example.betting_service.repository.BetRepository;
import com.example.betting_service.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BetService {

    private final BetRepository betRepository;
    private final MatchRepository matchRepository;

    // ✅ Place Bet
    public Bet placeBet(String userId, String matchId, String team, String oddAtBet, double amount) {
        Bet bet = Bet.builder()
                .userId(userId)
                .matchId(matchId)
                .betId("BET-" + UUID.randomUUID())
                .team(team)
                .oddAtBet(oddAtBet)
                .amount(amount)
                .placedAt(LocalDateTime.now())
                .cashout(false)
                .cashoutAmount(0)
                .winAmount(0)
                .isSettled(false)
                .won(false)
                .build();

        return betRepository.save(bet);
    }

    // ✅ Get all bets for user
    public List<Bet> getBetsByUser(String userId) {
        return betRepository.findByUserId(userId);
    }

    // ✅ Get single bet by betId
    public Optional<Bet> getBetByBetId(String betId) {
        return betRepository.findByBetId(betId);
    }

    // ✅ Cashout logic
    public String cashoutBet(String userId, String betId) {
        Optional<Bet> betOpt = betRepository.findById(betId);

        if (betOpt.isEmpty()) {
            return "❌ Bet not found!";
        }

        Bet bet = betOpt.get();

        if (!bet.getUserId().equals(userId)) {
            return "❌ Unauthorized cashout!";
        }

        if (bet.isCashout()) {
            return "❌ Bet already cashed out!";
        }

        Optional<Match> matchOpt = matchRepository.findById(bet.getMatchId());

        if (matchOpt.isEmpty()) {
            return "❌ Match not found!";
        }

        Match match = matchOpt.get();

        String latestOdd = null;

        if (bet.getTeam().equalsIgnoreCase(match.getTeam1())) {
            latestOdd = match.getOdd1();
        } else if (bet.getTeam().equalsIgnoreCase(match.getTeam2())) {
            latestOdd = match.getOdd2();
        }

        if (latestOdd == null || latestOdd.equals("❌") || latestOdd.equals("N/A")) {
            return "❌ Current odds not available for cashout!";
        }

        double cashoutAmount = bet.getAmount() * Double.parseDouble(latestOdd);

        bet.setCashout(true);
        bet.setCashoutAmount(cashoutAmount);
        bet.setCashoutAt(LocalDateTime.now());

        betRepository.save(bet);

        return "✅ Cashout successful! Cashout Amount: " + cashoutAmount;
    }

    // ✅ Settlement logic
    public String settleBetsForMatch(String matchId, String winningTeam) {
        List<Bet> bets = betRepository.findByMatchId(matchId);
        for (Bet bet : bets) {
            if (!bet.isSettled()) {
                if (bet.getTeam().equalsIgnoreCase(winningTeam)) {
                    double winAmount = bet.getAmount() * Double.parseDouble(bet.getOddAtBet());
                    bet.setWon(true);
                    bet.setWinAmount(winAmount);
                } else {
                    bet.setWon(false);
                    bet.setWinAmount(0);
                }
                bet.setSettled(true);
                betRepository.save(bet);
            }
        }
        return "✅ Settlement completed for match: " + matchId;
    }


}
