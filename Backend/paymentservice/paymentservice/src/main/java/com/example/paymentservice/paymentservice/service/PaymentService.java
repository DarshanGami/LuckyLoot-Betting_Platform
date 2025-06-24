package com.example.paymentservice.paymentservice.service;

import com.example.paymentservice.paymentservice.model.User;
import com.example.paymentservice.paymentservice.repository.UserRepository;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import java.util.Base64;
import java.util.Optional;

@Service
public class PaymentService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public PaymentService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public boolean handlePaymentSuccess(String userId, double amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setWalletBalance(user.getWalletBalance() + amount);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String createOrder(double amount) {
        try {
            String auth = razorpayKey + ":" + razorpaySecret;
            String base64Creds = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + base64Creds);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Razorpay uses paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

            HttpEntity<String> request = new HttpEntity<>(orderRequest.toString(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.razorpay.com/v1/orders", request, String.class);

            return response.getBody(); // returns order JSON
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create order: " + e.getMessage();
        }
    }

    public boolean withdrawToBank(String userId, double amount) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return false;

        User user = userOptional.get();
        if (user.getWalletBalance() < amount) return false;

        try {
            String auth = razorpayKey + ":" + razorpaySecret;
            String base64Creds = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + base64Creds);

            // STEP 1: Create Contact
            JSONObject contactPayload = new JSONObject();
            contactPayload.put("name", user.getName());
            contactPayload.put("email", user.getEmail());
            contactPayload.put("type", "customer");

            HttpEntity<String> contactRequest = new HttpEntity<>(contactPayload.toString(), headers);
            ResponseEntity<String> contactResponse = restTemplate.postForEntity(
                    "https://api.razorpay.com/v1/contacts", contactRequest, String.class);

            String contactId = new JSONObject(contactResponse.getBody()).getString("id");

            // STEP 2: Create Fund Account
            JSONObject bankAccount = new JSONObject();
            bankAccount.put("name", user.getName());
            bankAccount.put("ifsc", user.getIfscCode());
            bankAccount.put("account_number", user.getBankAccount());

            JSONObject fundAccountPayload = new JSONObject();
            fundAccountPayload.put("contact_id", contactId);
            fundAccountPayload.put("account_type", "bank_account");
            fundAccountPayload.put("bank_account", bankAccount);

            HttpEntity<String> fundAccountRequest = new HttpEntity<>(fundAccountPayload.toString(), headers);
            ResponseEntity<String> fundAccountResponse = restTemplate.postForEntity(
                    "https://api.razorpay.com/v1/fund_accounts", fundAccountRequest, String.class);

            String fundAccountId = new JSONObject(fundAccountResponse.getBody()).getString("id");

            // STEP 3: Create Payout
            JSONObject payoutPayload = new JSONObject();
            payoutPayload.put("account_number", "YOUR_RAZORPAY_ACCOUNT_NUMBER");
            payoutPayload.put("fund_account_id", fundAccountId);
            payoutPayload.put("amount", (int)(amount * 100));
            payoutPayload.put("currency", "INR");
            payoutPayload.put("mode", "IMPS");
            payoutPayload.put("purpose", "payout");
            payoutPayload.put("queue_if_low_balance", true);

            HttpEntity<String> payoutRequest = new HttpEntity<>(payoutPayload.toString(), headers);
            restTemplate.postForEntity("https://api.razorpay.com/v1/payouts", payoutRequest, String.class);

            // Update wallet
            user.setWalletBalance(user.getWalletBalance() - amount);
            userRepository.save(user);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


