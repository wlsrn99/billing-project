package com.billing.billingproject.user.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends Timestamped{

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

	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime statusChangedAt;

	@Builder
	public User(String email, String password, String refreshToken, UserType userType,
		LocalDateTime statusChangedAt) {
		this.email = email;
		this.password = password;
		this.refreshToken = refreshToken;
		this.userType = userType;
		this.statusChangedAt = statusChangedAt;
	}

	//회원 상태 변경 - 인증 회원
	public void ActiveUser() {
		this.userType = UserType.ACTIVE;
	}

	//로그인시 리프레시 토큰 초기화
	@Transactional
	public void refreshTokenReset(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
