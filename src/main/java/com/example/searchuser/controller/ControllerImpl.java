package com.example.searchuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchuser.dto.User;
import com.example.searchuser.service.ServiceTest;

@RestController
@RequestMapping("user")
public class ControllerImpl implements Controller {
	
	private final ServiceTest serviceTest;

	public ControllerImpl(ServiceTest serviceTest) {
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
	public User findUser(@PathVariable String userId) {
		User user = serviceTest.findUser(userId);
		return user;
	}	
}
