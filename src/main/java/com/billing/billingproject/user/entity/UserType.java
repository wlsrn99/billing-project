package com.billing.billingproject.user.entity;

public enum UserType {
	UNVERIFIED, //로그아웃, 회원가입
	ACTIVE; //로그인

	public String getAuthority() {
		return null;
	}
}
