package com.example.paymentservice.paymentservice.config;

import com.razorpay.RazorpayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        // Replace with your Razorpay test credentials
        String key = "rzp_test_ps3KfdfoWuaHm0";
        String secret = "6WhbaXNOJkbuTKg7AeYNpZjL";

        return new RazorpayClient(key, secret);
    }
}

