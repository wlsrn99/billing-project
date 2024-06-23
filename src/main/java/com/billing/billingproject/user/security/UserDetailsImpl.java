package com.billing.billingproject.user.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.billing.billingproject.user.entity.User;
import com.billing.billingproject.user.entity.UserType;

import lombok.Getter;

@Getter
public class UserDetailsImpl implements UserDetails {
	private final User user;

	public UserDetailsImpl(User user) {
		this.user = user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail(); // 1단계
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		UserType role = user.getUserType();
		String authority = role.name();

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}
}
