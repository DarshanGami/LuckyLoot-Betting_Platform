package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class BetGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.bet.url}")
    private String betServiceBaseUrl;

    // ✅ 1. Place Bet
    @PostMapping("/bet/place")
    public Mono<ResponseEntity<String>> placeBet(@RequestHeader("Authorization") String authHeader,
                                                 @RequestAttribute String userId,
                                                 @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/place")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 2. Get All Bets for User
    @GetMapping("/bet/my-bets")
    public Mono<ResponseEntity<String>> getUserBets(@RequestHeader("Authorization") String authHeader,
                                                    @RequestAttribute String userId) {
        return webClientBuilder.build()
                .get()
                .uri(betServiceBaseUrl + "/bet/my-bets")
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 3. Get Single Bet by betId
    @GetMapping("/bet/get/{betId}")
    public Mono<ResponseEntity<String>> getSingleBet(@RequestHeader("Authorization") String authHeader,
                                                     @RequestAttribute String userId,
                                                     @PathVariable String betId) {
        return webClientBuilder.build()
                .get()
                .uri(betServiceBaseUrl + "/bet/single/" + betId)
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 4. Cashout Bet (note: Internal expects RequestParam)
    @PostMapping("/bet/cashout")
    public Mono<ResponseEntity<String>> cashoutBet(@RequestHeader("Authorization") String authHeader,
                                                   @RequestAttribute String userId,
                                                   @RequestParam String betId) {
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/cashout?betId=" + betId)
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }


    // ✅ 5. Settle Bets (Admin/Internal)
    @PostMapping("/bet/settle")
    public Mono<ResponseEntity<String>> settleMatch(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/settle")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 6. Get All Matches (for frontend)
    @GetMapping("match/all-matches")
    public Mono<ResponseEntity<String>> getAllMatches() {
        return webClientBuilder.build()
                .get()
                .uri(betServiceBaseUrl + "/match/all")
                .retrieve()
                .toEntity(String.class);
    }
}
