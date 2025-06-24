package com.example.paymentservice.paymentservice.repository;

import com.example.paymentservice.paymentservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
