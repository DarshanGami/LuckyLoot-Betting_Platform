package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public String saveUser(@RequestBody User user) {
        userRepository.save(user);
        return "User saved!";
    }
}
