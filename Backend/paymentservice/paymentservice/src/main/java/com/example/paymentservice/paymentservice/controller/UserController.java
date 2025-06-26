package com.example.paymentservice.paymentservice.controller;

import com.example.paymentservice.paymentservice.model.User;
import com.example.paymentservice.paymentservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> request) {
        User user = User.builder()
                .name(request.get("name"))
                .email(request.get("email"))
                .totalDeposit(0.0)
                .transactions(new ArrayList<>())
                .build();

        return ResponseEntity.ok(userRepository.save(user));
    }
}

