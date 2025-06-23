// Profile.java
package com.example.profile_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String secondaryEmail;
    private String avatarUrl;
    private Address address;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class Address {
        private String line1;
        private String city;
        private String state;
        private String country;
        private String pincode;
    }
}