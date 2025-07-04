package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/gateway/auth")
@RequiredArgsConstructor
public class AuthGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.auth.url}")
    private String authServiceBaseUrl;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceBaseUrl + "/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceBaseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class));
    }


    @GetMapping("/verify")
    public Mono<ResponseEntity<String>> verifyUser(@RequestParam String token) {
        return webClientBuilder
                .baseUrl(authServiceBaseUrl)                // ✅ Set base URL here
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/verify")               // ✅ Only path here
                        .queryParam("token", token)
                        .build())
                .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class));
    }



}
