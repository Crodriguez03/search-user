package com.example.searchuser.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 8933463067109377391L;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
