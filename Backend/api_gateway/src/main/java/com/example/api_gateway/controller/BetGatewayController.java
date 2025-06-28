package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway/bet")
@RequiredArgsConstructor
public class BetGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.bet.url}")
    private String betServiceBaseUrl;

    // ✅ 1. Place Bet (requires userId from JWT)
    @PostMapping("/place")
    public Mono<ResponseEntity<String>> placeBet(@RequestHeader("Authorization") String authHeader,
                                                 @RequestAttribute String userId,
                                                 @RequestBody String requestBody) {
        // ✅ Inject userId into header for Bet Service if needed (or Bet Service directly uses it)
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/place")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 2. Get Bets for user (needs userId from JWT)
    @GetMapping("/user-bets")
    public Mono<ResponseEntity<String>> getUserBets(@RequestHeader("Authorization") String authHeader,
                                                    @RequestAttribute String userId) {
        return webClientBuilder.build()
                .get()
                .uri(betServiceBaseUrl + "/bet/user")
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 3. Get Single Bet (betId as query param or path param)
    @GetMapping("/get/{betId}")
    public Mono<ResponseEntity<String>> getSingleBet(@PathVariable String betId) {
        return webClientBuilder.build()
                .get()
                .uri(betServiceBaseUrl + "/bet/" + betId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 4. Cashout Bet (userId from JWT)
    @PostMapping("/cashout/{betId}")
    public Mono<ResponseEntity<String>> cashout(@RequestHeader("Authorization") String authHeader,
                                                @RequestAttribute String userId,
                                                @PathVariable String betId) {
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/cashout/" + betId)
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 5. Settle Bets for Match (No JWT needed - Admin/Internal use)
    @PostMapping("/settle")
    public Mono<ResponseEntity<String>> settleMatch(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(betServiceBaseUrl + "/bet/settle")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }
}
