package com.example.betting_service.client;

import com.example.betting_service.dto.WalletCreditRequest;
import com.example.betting_service.dto.WalletDeductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WalletClient {

    private final WebClient webClient;

    @Autowired
    public WalletClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();  // Wallet-service URL
    }

    public String deductAmount(String userId, WalletDeductRequest request) {
        return webClient.post()
                .uri("/wallet/deduct")
                .header("userId", userId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // Blocking for simplicity
    }

    public String creditAmount(String userId, WalletCreditRequest request) {
        return webClient.post()
                .uri("/wallet/credit")
                .header("userId", userId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
