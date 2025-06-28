package com.example.auth_service.dto;

import lombok.Data;

@Data
public class CreateProfileRequest {
    private String name;

    //    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Invalid phone number")
    private String phone;

    private String dob;
    private String gender;

    //    @Email(message = "Invalid secondary email")
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
