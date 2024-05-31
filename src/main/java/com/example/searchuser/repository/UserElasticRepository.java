package com.example.searchuser.repository;

import com.example.searchuser.dto.User;

public interface UserElasticRepository {

	User index(User user);

	User findById(String id);

}
