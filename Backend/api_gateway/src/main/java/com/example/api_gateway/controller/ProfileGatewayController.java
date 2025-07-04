package com.example.api_gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/gateway/profile")
@RequiredArgsConstructor
public class ProfileGatewayController {

    private final WebClient.Builder webClientBuilder;

    @Value("${service.profile.url}")
    private String profileServiceBaseUrl;

    // ✅ Create Profile (called during signup)
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createProfile(@RequestHeader("Authorization") String authHeader,
                                                      @RequestAttribute String userId,
                                                      @RequestAttribute String email,
                                                      @RequestBody String requestBody) {
        return webClientBuilder.build()
                .post()
                .uri(profileServiceBaseUrl + "/profile/create")
                .header("userId", userId)
                .header("email", email)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Get Own Profile
    @GetMapping
    public Mono<ResponseEntity<String>> getOwnProfile(@RequestHeader("Authorization") String authHeader,
                                                      @RequestAttribute String userId) {
        return webClientBuilder.build()
                .get()
                .uri(profileServiceBaseUrl + "/profile")
                .header("userId", userId)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Update Own Profile
    @PutMapping
    public Mono<ResponseEntity<String>> updateOwnProfile(@RequestHeader("Authorization") String authHeader,
                                                         @RequestAttribute String userId,
                                                         @RequestBody String requestBody) {
        return webClientBuilder.build()
                .put()
                .uri(profileServiceBaseUrl + "/profile")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(String.class);
    }

    // ✅ Get Profile by userId (admin or internal use)
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<String>> getProfileById(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable String userId) {
        return webClientBuilder.build()
                .get()
                .uri(profileServiceBaseUrl + "/profile/" + userId)
                .retrieve()
                .toEntity(String.class);
    }
}
