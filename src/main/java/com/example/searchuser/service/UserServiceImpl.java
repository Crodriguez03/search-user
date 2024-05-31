package com.example.searchuser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.searchuser.cache.CacheUserService;
import com.example.searchuser.dto.User;
import com.example.searchuser.repository.UserElasticRepository;
import com.example.searchuser.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	private final CacheUserService cacheUserService;
	
	private final UserRepository userRepository;
	
	private final UserElasticRepository userElasticRepository;
	
	public UserServiceImpl(CacheUserService cacheUserService, UserRepository userRepository, UserElasticRepository userElasticRepository) {
		this.cacheUserService = cacheUserService;
		this.userRepository = userRepository;
		this.userElasticRepository = userElasticRepository;
	}
	
	@Override
	public void prueba() {
		User user = new User();
		
		user.setId("13743647868");
		userElasticRepository.index(user);
		userRepository.save(user);
		
		cacheUserService.updateUserFromCacheTemplate(user);
		
		System.out.println(user);

		
	}

	@Override
	public User findUser(String userId) {
		// TODO Auto-generated method stub
		userRepository.findById(userId);
		
		cacheUserService.findUserFromCacheTemplate(userId);
		return userElasticRepository.findById(userId);
	}
}
