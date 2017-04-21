package com.vng.gsmobile.casino.entity;

import java.util.List;

public class UserResponse {
	int returnCode;
	String message;
	List<UserDTO> data;
	public int getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<UserDTO> getData() {
		return data;
	}
	public void setData(List<UserDTO> data) {
		this.data = data;
	}	
}
