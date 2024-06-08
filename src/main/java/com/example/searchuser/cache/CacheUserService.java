package com.example.searchuser.cache;

import java.util.Collection;
import java.util.List;

import com.example.searchuser.dto.UserDTO;

public interface CacheUserService {

	UserDTO findUserFromCache(String id);
	
	List<UserDTO> findUsersFromCache(Collection<String> ids);
	
	void invalidateUserFromCache(String id);
	
	void updateUserFromCache(UserDTO user);
	
	void updateUsersFromCache(Collection<UserDTO> users);
}
