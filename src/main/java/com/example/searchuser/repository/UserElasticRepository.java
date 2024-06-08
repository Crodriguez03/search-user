package com.example.searchuser.repository;

import java.util.Collection;
import java.util.List;

import com.example.searchuser.model.UserIndex;

public interface UserElasticRepository {

	void index(UserIndex user);

	List<String> findUsersIdStartNameWith(String startName);

	void indexBulk(Collection<UserIndex> users);

}
