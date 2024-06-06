package com.example.searchuser.controller;

import com.example.searchuser.dto.UserDTO;

public interface UserController {

	String prueba();

	UserDTO findUser(String userId);
	
}
