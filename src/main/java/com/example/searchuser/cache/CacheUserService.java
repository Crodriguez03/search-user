package com.example.searchuser.cache;

import java.util.Collection;
import java.util.List;

import com.example.searchuser.dto.User;

public interface CacheUserService {

	User findUserFromCacheTemplate(String id);
	
	List<User> findUsersFromCacheTemplate(Collection<String> ids);
	
	void invalidateUserFromCache(String id);
	
	void updateUserFromCacheTemplate(User user);
	
	void updateUserFromCacheTemplate(User user, Long minutesTTL);
	
	void updateUsersFromCacheTemplate(Collection<User> users);
	
	void updateUsersFromCacheTemplate(Collection<User> users, Long minutesTTL);
}
