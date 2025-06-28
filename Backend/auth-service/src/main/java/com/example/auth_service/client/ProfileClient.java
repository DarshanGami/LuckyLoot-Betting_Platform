package com.example.auth_service.client;

import com.example.auth_service.dto.CreateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProfileClient {

    private final WebClient webClient;

    @Autowired
    public ProfileClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();  // Change port if different
    }

    public void createEmptyProfile(String userId, String email) {
        CreateProfileRequest request = new CreateProfileRequest();
        // Set default fields if needed (optional)

        webClient.post()
                .uri("/profile/create")
                .header("userId", userId)
                .header("email", email)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)  // Assuming you don't care about response
                .block();
    }
}
