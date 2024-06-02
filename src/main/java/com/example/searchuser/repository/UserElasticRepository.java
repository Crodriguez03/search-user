package com.example.searchuser.repository;

import com.example.searchuser.model.User;

public interface UserElasticRepository {

	User index(User user);

	User findById(String id);

}
