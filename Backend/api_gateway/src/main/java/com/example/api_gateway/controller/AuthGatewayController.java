package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway/auth")
@RequiredArgsConstructor
public class AuthGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.auth.url}")
    private String authServiceBaseUrl;

    @PostMapping("/register")
    public Mono<String> register(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceBaseUrl + "/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceBaseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/verify")
    public Mono<String> verifyUser(@RequestParam String token) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(authServiceBaseUrl + "/auth/verify")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
