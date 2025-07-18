package com.example.auth_service.controller;

import com.example.auth_service.client.ProfileClient;
import com.example.auth_service.dto.ApiResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.EmailService;
import com.example.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private ProfileClient profileClient;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ Email is required!");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(409)
                    .body("❌ Email already in use!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setVerified(false); // for email verification
        User savedUser=userRepository.save(newUser);


        profileClient.createEmptyProfile(newUser.getId(), newUser.getEmail());

        // ✅ Generate token & send email
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail());
        emailService.sendVerificationEmail(request.getEmail(), token);

        return ResponseEntity.ok("✅ User registered successfully!");
    }




    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        var userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body("❌ Invalid email or password!");
        }

        User user = userOptional.get();

        if (!user.isVerified()) {
            return ResponseEntity
                    .status(403)
                    .body("❌ Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(401)
                    .body("❌ Invalid email or password!");
        }

        String token = jwtUtil.generateToken(user.getId(),user.getEmail());

        return ResponseEntity.ok(token);
    }



    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isVerified()) {
                return ResponseEntity
                        .ok("✅ Email already verified!");
            }

            user.setVerified(true);
            userRepository.save(user);

            return ResponseEntity
                    .ok("✅ Email verified successfully!");
        } else {
            return ResponseEntity
                    .status(404)
                    .body("❌ User not found");
        }
    }


}
