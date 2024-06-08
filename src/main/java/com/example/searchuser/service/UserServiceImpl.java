package com.example.searchuser.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.searchuser.cache.CacheUserService;
import com.example.searchuser.dto.UserDTO;
import com.example.searchuser.model.User;
import com.example.searchuser.model.UserIndex;
import com.example.searchuser.repository.UserElasticRepository;
import com.example.searchuser.repository.UserRepository;
import com.example.searchuser.utils.ListUtils;

@Service
public class UserServiceImpl implements UserService {
	
	private final CacheUserService cacheUserService;
	
	private final UserRepository userRepository;
	
	private final UserElasticRepository userElasticRepository;
	
	public UserServiceImpl(CacheUserService cacheUserService, UserRepository userRepository, UserElasticRepository userElasticRepository) {
		this.cacheUserService = cacheUserService;
		this.userRepository = userRepository;
		this.userElasticRepository = userElasticRepository;
	}

	@Override
	public void report(String startName) {
		
		List<String> usersId = userElasticRepository.findUsersIdStartNameWith(startName);
		
		List<List<String>> listUsersId = ListUtils.chopped(usersId, 50);
		
		Set<UserDTO> users = listUsersId.parallelStream().flatMap(subList -> findUsers(subList).stream()).collect(Collectors.toSet());
		
		try {
			createReport(users, startName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Set<UserDTO> findUsers(List<String> usersId) {
		
		Set<UserDTO> result = ConcurrentHashMap.newKeySet();
		Set<String> notFounds = new HashSet<>();

        //buscamos los usersId en cache
        List<UserDTO> items = cacheUserService.findUsersFromCache(usersId);
        for (int i = 0; i < usersId.size(); i++) {
        	UserDTO item = items.get(i);
        	if(item != null){
                result.add(item);
            } else {
                notFounds.add(usersId.get(i));
            }
		}

        //los que no encuentra, los busca, los añade al resultado y los cachea
        if (!CollectionUtils.isEmpty(notFounds)) {
        	Iterable<User> users = userRepository.findAllById(notFounds);
        	Set<UserDTO> notFoundUsers = new HashSet<>();
        	users.forEach(user -> notFoundUsers.add(modelToDTO(user)));
        	if (!CollectionUtils.isEmpty(notFoundUsers)) {
        		result.addAll(notFoundUsers);
        		cacheUserService.updateUsersFromCache(notFoundUsers);
        	}
        }
        return result;
	}

	private UserDTO modelToDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setAddress(user.getAddress());
		dto.setBirthDate(user.getBirthDate());
		return dto;
	}
	
	private void createReport(Collection<UserDTO> users, String startName) throws IOException {
		File file = new File("UsersReport_" + startName + ".txt");
		
		FileWriter fileWriter = new FileWriter(file);
		users.stream().forEach(user -> {
			try {
				fileWriter.write(user.toString() + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		fileWriter.close();
	}

	@Override
	public void createUsers() {
		
		List<User> users = new ArrayList<>();
		IntStream.range(1, 20001).forEach(i -> users.add(createUser(i)));
		
		List<List<User>> lists = ListUtils.chopped(users, 500);
		
		lists.forEach(this::createUsersBDs);
	}
	
	private void createUsersBDs(Collection<User> users) {
		users = userRepository.saveAll(users);
		
		List<UserIndex> usersIndex = users.stream().map(user -> new UserIndex(user.getId(), user.getName())).toList();
		
		userElasticRepository.indexBulk(usersIndex);
	}
	
	private User createUser(int i) {
		User user = new User();
		user.setName("Name_" + i);
		user.setSurname("Surname " + i);
		user.setBirthDate(Instant.now().minus(7000L, ChronoUnit.DAYS));
		user.setAddress("Address_" + i);
		
		return user;
	}
}
