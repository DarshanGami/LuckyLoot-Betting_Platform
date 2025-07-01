package com.example.paymentservice.client;

import com.example.paymentservice.dto.WalletCreditRequest;
import com.example.paymentservice.dto.WalletDeductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WalletClient {

    private final WebClient webClient;

    @Autowired
    public WalletClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public double getUserBalance(String userId) {
        return webClient.get()
                .uri("/wallet/balance")
                .header("userId", userId)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
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

    public String deductAmount(String userId, WalletDeductRequest request) {
        return webClient.post()
                .uri("/wallet/deduct")
                .header("userId", userId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
