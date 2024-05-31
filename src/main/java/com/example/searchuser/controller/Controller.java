package com.example.searchuser.controller;

import com.example.searchuser.dto.User;

public interface Controller {

	String prueba();

	User findUser(String userId);
	
}
