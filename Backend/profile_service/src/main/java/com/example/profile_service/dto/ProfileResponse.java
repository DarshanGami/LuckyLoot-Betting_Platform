// ProfileResponse.java
package com.example.profile_service.dto;

import lombok.Data;

@Data
public class ProfileResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String secondaryEmail;
    private String avatarUrl;
    private Address address;

    @Data
    public static class Address {
        private String line1;
        private String city;
        private String state;
        private String country;
        private String pincode;
    }
}
