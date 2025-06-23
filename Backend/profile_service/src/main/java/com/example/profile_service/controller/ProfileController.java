// ProfileController.java
package com.example.profile_service.controller;

import com.example.profile_service.dto.ProfileRequest;
import com.example.profile_service.dto.ProfileResponse;
import com.example.profile_service.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/create")
    public ProfileResponse create(@RequestHeader("userId") String userId,
                                  @RequestHeader("email") String email,
                                  @RequestBody @Valid ProfileRequest request) {
        return profileService.createProfile(userId, email, request);
    }

    @GetMapping
    public ProfileResponse getOwn(@RequestHeader("userId") String userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping
    public ProfileResponse updateOwn(@RequestHeader("userId") String userId,
                                     @RequestBody @Valid ProfileRequest request) {
        return profileService.updateProfile(userId, request);
    }

    @GetMapping("/{userId}")
    public ProfileResponse getById(@PathVariable String userId) {
        return profileService.getProfile(userId);
    }
}
