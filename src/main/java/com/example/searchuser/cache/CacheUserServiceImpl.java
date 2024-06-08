package com.example.searchuser.cache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.random.RandomGenerator;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.searchuser.dto.UserDTO;

@Component
public class CacheUserServiceImpl implements CacheUserService {

	private static final String PREFIX_USER = "user:";
	
	private final RandomGenerator random = RandomGenerator.getDefault();

	private RedisTemplate<String, UserDTO> redisTemplate;
	
	public CacheUserServiceImpl(RedisTemplate<String, UserDTO> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void invalidateUserFromCache(String id) {
		redisTemplate.delete(PREFIX_USER + id);
	}

	@Override
	public UserDTO findUserFromCache(String id) {
		return redisTemplate.opsForValue().get(PREFIX_USER + id);
	}

	@Override
	public List<UserDTO> findUsersFromCache(Collection<String> ids) {
		List<String> keys = ids.stream().map(id -> PREFIX_USER + id).toList();
		return redisTemplate.opsForValue().multiGet(keys);
	}

	@Async
	@Override
	public void updateUserFromCache(UserDTO user) {
		// Seteamos una duración de la cache aleatoria para pruebas
		redisTemplate.opsForValue().set(PREFIX_USER + user.getId(), user, Duration.of(random.nextInt(5, 59), ChronoUnit.SECONDS));
	}

	@Async
	@Override
	public void updateUsersFromCache(Collection<UserDTO> users) {
		redisTemplate.execute(new SessionCallback<List<Object>>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi();
				for (UserDTO user : users) {
					// Seteamos una duración de la cache aleatoria para pruebas
					operations.opsForValue().set(PREFIX_USER + user.getId(), user,
							Duration.of(random.nextInt(5, 59), ChronoUnit.SECONDS));
				}
				return operations.exec();
			}
		});
	}
}
