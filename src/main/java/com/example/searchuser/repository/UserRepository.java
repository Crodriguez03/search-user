package com.example.searchuser.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.searchuser.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
