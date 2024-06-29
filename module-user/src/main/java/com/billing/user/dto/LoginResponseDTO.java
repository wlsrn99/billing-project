package com.billing.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
	private String email;
	private String accessToken;

	public LoginResponseDTO(String email, String accessToken) {
		this.email = email;
		this.accessToken = accessToken;
	}
}
