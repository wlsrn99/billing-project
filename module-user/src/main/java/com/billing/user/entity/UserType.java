package com.billing.user.entity;


public enum UserType {
	USER, //시청자
	SELEER; //판매자

	public String getAuthority() {
		return "ROLE_" + this.name();
	}
}
