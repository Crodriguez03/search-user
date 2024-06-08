package com.example.searchuser.dto;

import java.io.Serializable;
import java.time.Instant;

public class UserDTO implements Serializable {
	
	private static final long serialVersionUID = -5468706110183848462L;
	
	private String id;
	
	private String name;
	
	private String surname;
	
	private Instant birthDate;
	
	private String address;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Instant getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Instant birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return name + ", " + surname + ", " + birthDate + ", " + address;
	}
}
