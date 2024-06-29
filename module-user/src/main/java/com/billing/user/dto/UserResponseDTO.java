package com.billing.user.dto;

import java.time.LocalDateTime;

import com.billing.user.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
	private Long id;
	private String email;
	private String userType;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public UserResponseDTO(UserEntity userEntity) {
		this.id = userEntity.getId();
		this.email = userEntity.getEmail();
		this.userType = userEntity.getUserType().toString();
		this.createdAt = userEntity.getCreatedAt();
		this.modifiedAt = userEntity.getModifiedAt();
	}

}
