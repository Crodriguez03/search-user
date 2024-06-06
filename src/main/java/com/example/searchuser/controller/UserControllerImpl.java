package com.example.searchuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchuser.dto.UserDTO;
import com.example.searchuser.service.UserService;

@RestController
@RequestMapping("user")
public class UserControllerImpl implements UserController {
	
	private final UserService serviceTest;

	public UserControllerImpl(UserService serviceTest) {
		this.serviceTest = serviceTest;
	}
	
	@Override
	@GetMapping()
	public String prueba() {
		serviceTest.prueba();
		return "OK";
	}
	
	@Override
	@GetMapping("{userId}")
	public UserDTO findUser(@PathVariable String userId) {
		return serviceTest.findUser(userId);
	}	
}
