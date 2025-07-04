package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/gateway/wallet")
@RequiredArgsConstructor
public class WalletGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.wallet.url}")
    private String walletServiceBaseUrl;

    // ✅ Create Wallet
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createWallet(@RequestHeader("Authorization") String authHeader,
                                                     @RequestAttribute String userId) {
        return webClientBuilder.build()
                .post()
                .uri(walletServiceBaseUrl + "/wallet/create")
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Get Wallet Balance
    @GetMapping("/balance")
    public Mono<ResponseEntity<String>> getWalletBalance(@RequestHeader("Authorization") String authHeader,
                                                         @RequestAttribute String userId) {
        return webClientBuilder.build()
                .get()
                .uri(walletServiceBaseUrl + "/wallet/balance")
                .header("userId", userId) // Pass userId as header
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Credit Wallet
    @PostMapping("/credit")
    public Mono<ResponseEntity<String>> creditWallet(@RequestHeader("Authorization") String authHeader,
                                                     @RequestAttribute String userId,
                                                     @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(walletServiceBaseUrl + "/wallet/credit")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Deduct Wallet
    @PostMapping("/deduct")
    public Mono<ResponseEntity<String>> deductWallet(@RequestHeader("Authorization") String authHeader,
                                                     @RequestAttribute String userId,
                                                     @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(walletServiceBaseUrl + "/wallet/deduct")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Get Transaction History
    @GetMapping("/transactions")
    public Mono<ResponseEntity<String>> getTransactions(@RequestHeader("Authorization") String authHeader,
                                                        @RequestAttribute String userId) {
        return webClientBuilder.build()
                .get()
                .uri(walletServiceBaseUrl + "/wallet/transactions")
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }
}
