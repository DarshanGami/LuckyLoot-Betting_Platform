// ProfileService.java
package com.example.profile_service.service;

import com.example.profile_service.dto.ProfileRequest;
import com.example.profile_service.dto.ProfileResponse;
import com.example.profile_service.model.Profile;
import com.example.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repository;

    public ProfileResponse createProfile(String userId, String email, ProfileRequest request) {
        Profile profile = new Profile();

        System.out.println("Address from request: " + request.getAddress());

        profile.setId(userId);
        profile.setName(request.getName());
        profile.setEmail(email);
        profile.setPhone(request.getPhone());
        profile.setDob(request.getDob());
        profile.setGender(request.getGender());
        profile.setSecondaryEmail(request.getSecondaryEmail());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setAddress(convertAddress(request.getAddress()));
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(profile));
    }

    public ProfileResponse getProfile(String userId) {
        Profile profile = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }

    public ProfileResponse updateProfile(String userId, ProfileRequest request) {
        Profile profile = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setName(request.getName());
        profile.setPhone(request.getPhone());
        profile.setDob(request.getDob());
        profile.setGender(request.getGender());
        profile.setSecondaryEmail(request.getSecondaryEmail());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setAddress(convertAddress(request.getAddress()));
        profile.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(profile));
    }

    private Profile.Address convertAddress(ProfileRequest.Address input) {
        if (input == null) return null;
        Profile.Address address = new Profile.Address();
        address.setLine1(input.getLine1());
        address.setCity(input.getCity());
        address.setState(input.getState());
        address.setCountry(input.getCountry());
        address.setPincode(input.getPincode());
        return address;
    }

    private ProfileResponse toResponse(Profile p) {
        ProfileResponse res = new ProfileResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setEmail(p.getEmail());
        res.setPhone(p.getPhone());
        res.setDob(p.getDob());
        res.setGender(p.getGender());
        res.setSecondaryEmail(p.getSecondaryEmail());
        res.setAvatarUrl(p.getAvatarUrl());
        res.setAddress(convertAddress(p.getAddress()));
        return res;
    }

    private ProfileResponse.Address convertAddress(Profile.Address input) {
        if (input == null) return null;
        ProfileResponse.Address address = new ProfileResponse.Address();
        address.setLine1(input.getLine1());
        address.setCity(input.getCity());
        address.setState(input.getState());
        address.setCountry(input.getCountry());
        address.setPincode(input.getPincode());
        return address;
    }
}