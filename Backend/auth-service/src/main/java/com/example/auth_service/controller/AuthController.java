package com.example.auth_service.controller;

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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse("❌ Email is required!"));
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(409)
                    .body(new ApiResponse("❌ Email already in use!"));
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setVerified(false); // for email verification
        userRepository.save(newUser);

        // ✅ Generate token & send email
        String token = jwtUtil.generateToken(request.getEmail());
        emailService.sendVerificationEmail(request.getEmail(), token);

        return ResponseEntity.ok(new ApiResponse("✅ User registered successfully!"));
    }




    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        var userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponse("❌ Invalid email or password!"));
        }

        User user = userOptional.get();

        if (!user.isVerified()) {
            return ResponseEntity
                    .status(403)
                    .body(new ApiResponse("❌ Please verify your email before logging in."));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponse("❌ Invalid email or password!"));
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new ApiResponse("Bearer " + token));
    }



//just for testing
//    @GetMapping("/profile")
//    public String profile(@RequestHeader("Authorization") String authHeader) {
//
//        System.out.println("🔒 Inside /auth/profile endpoint");
//        String token = authHeader.substring(7);
//        String email = jwtUtil.extractEmail(token);
//        return "Logged in as: " + email;
//    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse("❌ Invalid or expired token"));
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isVerified()) {
                return ResponseEntity
                        .ok(new ApiResponse("✅ Email already verified!"));
            }

            user.setVerified(true);
            userRepository.save(user);

            return ResponseEntity
                    .ok(new ApiResponse("✅ Email verified successfully!"));
        } else {
            return ResponseEntity
                    .status(404)
                    .body(new ApiResponse("❌ User not found"));
        }
    }


}
