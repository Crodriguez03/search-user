package com.example.searchuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchuser.service.UserService;

@RestController
@RequestMapping("user")
public class UserControllerImpl implements UserController {
	
	private final UserService userService;

	public UserControllerImpl(UserService serviceTest) {
		this.userService = serviceTest;
	}
	
	@Override
	@GetMapping("report")
	public void report(@RequestParam String startName) {
		userService.report(startName);
	}
	
	@Override
	@GetMapping("createUsers")
	public void createUsers() {
		userService.createUsers();
	}
}
