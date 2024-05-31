package com.example.searchuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.searchuser.dto.User;

@Configuration
public class RedisConfig {

	@Bean
	LettuceConnectionFactory connectionFactory() {
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration());
	}

	@Bean
	RedisTemplate<String, User> redisTemplate(RedisConnectionFactory connectionFactory) {

		RedisTemplate<String, User> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
		return template;
	}
}
