package com.billing.billingproject.user.dto;

import java.time.LocalDateTime;

import com.billing.billingproject.user.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
	private Long id;
	private String email;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.createdAt = user.getCreatedAt();
		this.modifiedAt = user.getModifiedAt();
	}

}
