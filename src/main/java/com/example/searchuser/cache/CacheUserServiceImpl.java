package com.example.searchuser.cache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.searchuser.model.User;

@Component
public class CacheUserServiceImpl implements CacheUserService {

	private static final Long DEFAULT_TTL = 1L;

	private static final String PREFIX_USER = "user:";

	private RedisTemplate<String, User> redisTemplate;
	
	public CacheUserServiceImpl(RedisTemplate<String, User> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void invalidateUserFromCache(String id) {
		redisTemplate.delete(PREFIX_USER + id);
	}

	@Override
	public User findUserFromCacheTemplate(String id) {
		return redisTemplate.opsForValue().get(PREFIX_USER + id);
	}

	@Override
	public List<User> findUsersFromCacheTemplate(Collection<String> ids) {
		List<String> keys = ids.stream().map(id -> PREFIX_USER + id).toList();
		return redisTemplate.opsForValue().multiGet(keys);
	}

	@Async
	@Override
	public void updateUserFromCacheTemplate(User user) {
		updateUserFromCacheTemplate(user, DEFAULT_TTL);
	}

	@Async
	@Override
	public void updateUserFromCacheTemplate(User user, Long minutesTTL) {
		redisTemplate.opsForValue().set(PREFIX_USER + user.getId(), user, Duration.of(minutesTTL, ChronoUnit.DAYS));
	}

	@Async
	@Override
	public void updateUsersFromCacheTemplate(Collection<User> users) {
		updateUsersFromCacheTemplate(users, DEFAULT_TTL);
	}

	@Async
	@Override
	public void updateUsersFromCacheTemplate(Collection<User> users, Long minutesTTL) {
		redisTemplate.execute(new SessionCallback<List<Object>>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi();
				for (User user : users) {
					operations.opsForValue().set(PREFIX_USER + user.getId(), user,
							Duration.of(minutesTTL, ChronoUnit.DAYS));
				}
				return operations.exec();
			}
		});
	}
}
