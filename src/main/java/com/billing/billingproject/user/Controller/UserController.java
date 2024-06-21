package com.billing.billingproject.user.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.billing.billingproject.user.dto.UserDto;
import com.billing.billingproject.user.entity.User;
import com.billing.billingproject.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserRepository userRepository;

	@PostMapping("/test")
	public void registerUser(@RequestBody UserDto userDto) {
		User user = new User(userDto.getEmail());
		userRepository.save(user);
	}



}
