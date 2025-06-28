package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway/payment")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.payment.url}")
    private String paymentServiceBaseUrl;

    // ✅ 1. Create Deposit Order
    @PostMapping("/deposit")
    public Mono<ResponseEntity<String>> createDeposit(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(paymentServiceBaseUrl + "/api/payment/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 2. Confirm Deposit
    @PostMapping("/confirm")
    public Mono<ResponseEntity<String>> confirmDeposit(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(paymentServiceBaseUrl + "/api/payment/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ 3. Withdraw (userId comes from JWT → Request Attribute)
    @PostMapping("/withdraw")
    public Mono<ResponseEntity<String>> withdraw(@RequestHeader("Authorization") String authHeader,
                                                 @RequestAttribute String userId,
                                                 @RequestBody String requestBody) {
        // ✅ Overwrite/Inject correct userId into request body before forwarding
        String updatedRequestBody = requestBody.replaceFirst(
                "\"userId\"\\s*:\\s*\".*?\"",
                "\"userId\":\"" + userId + "\""
        );

        return webClientBuilder.build()
                .post()
                .uri(paymentServiceBaseUrl + "/api/payment/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedRequestBody)
                .retrieve()
                .toEntity(String.class);
    }
}
