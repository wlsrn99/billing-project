package com.billing.user.entity;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class UserEntity extends Timestamped{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@NotNull
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private String refreshToken;

	@Column(columnDefinition = "varchar(30)")
	@Enumerated(value = EnumType.STRING)
	private UserType userType;


	@Builder
	public UserEntity(String email, String password, String refreshToken, UserType userType) {
		this.email = email;
		this.password = password;
		this.refreshToken = refreshToken;
		this.userType = userType;
	}

	//로그인시 리프레시 토큰 초기화
	@Transactional
	public void refreshTokenReset(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
