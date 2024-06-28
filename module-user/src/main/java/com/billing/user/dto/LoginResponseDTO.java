package com.billing.user.dto;

import com.billing.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
	private String email;
	private String userType;

	public LoginResponseDTO(User user) {
		this.email = user.getEmail();
		this.userType = user.getUserType().toString();
	}
}
