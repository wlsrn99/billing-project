package com.billing.util;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.billing.Exception.UserErrorCode;
import com.billing.Exception.UserException;
import com.billing.repository.UserRepository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDetailServiceImpl implements ReactiveUserDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
	private final UserRepository userRepository;

	public UserDetailServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Mono<UserDetails> findByUsername(String email) {
		return Mono.fromCallable(() -> {
				return userRepository.findByEmail(email);
			})
			.subscribeOn(Schedulers.boundedElastic()) // 블로킹 호출을 처리하기 위해 별도의 스레드 풀을 사용
			.flatMap(userOptional -> {
				if (userOptional.isPresent()) {
					logger.info("User found for email: {}", email);
					UserDetailsImpl userDetails = UserDetailsImpl.builder()
						.password(userOptional.get().getPassword())
						.email(userOptional.get().getEmail())
						.build();
					return Mono.just(userDetails);
				} else {
					return Mono.error(new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
				}
			})
			.cast(UserDetails.class); // UserDetails 타입으로 캐스팅합니다.
	}
}
