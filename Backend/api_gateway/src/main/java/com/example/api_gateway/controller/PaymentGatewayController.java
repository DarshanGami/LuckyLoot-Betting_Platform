package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
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
    public Mono<ResponseEntity<String>> confirmDeposit(@RequestHeader("Authorization") String authHeader,
                                                       @RequestAttribute String userId,
                                                       @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(paymentServiceBaseUrl + "/api/payment/confirm")
                .header("userId", userId)  // ✅ Send userId in header
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)    // ✅ No more modifying body
                .retrieve()
                .toEntity(String.class);
    }


    // ✅ 3. Withdraw (userId comes from JWT → Request Attribute)
    @PostMapping("/withdraw")
    public Mono<ResponseEntity<String>> withdraw(@RequestHeader("Authorization") String authHeader,
                                                 @RequestAttribute String userId,
                                                 @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(paymentServiceBaseUrl + "/api/payment/withdraw")
                .header("userId", userId)  // ✅ Passing userId as header
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)    // ✅ Forwarding raw body
                .retrieve()
                .toEntity(String.class);
    }



}
