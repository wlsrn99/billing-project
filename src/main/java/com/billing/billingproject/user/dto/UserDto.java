package com.billing.billingproject.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
	private String email;

	public UserDto(String email) {
		this.email = email;
	}
}
