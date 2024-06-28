package com.billing.util;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.billing.exception.UserErrorCode;
import com.billing.exception.UserException;
import com.billing.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements ReactiveUserDetailsService {
	private final UserRepository userRepository;

	public Mono<UserDetails> findByUsername(String email) {
		return Mono.fromCallable(() -> userRepository.findByEmail(email))
			.subscribeOn(Schedulers.boundedElastic()) // 블로킹 -> 논블로킹으로 처리하기 위해 별도의 스레드 풀을 사용
			.flatMap(user -> {
				if (user.isPresent()) {
					UserDetailsImpl userDetails = UserDetailsImpl.builder()
						.password(user.get().getPassword())
						.email(user.get().getEmail())
						.build();
					return Mono.just(userDetails);
				} else {
					return Mono.error(new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
				}
			})
			.cast(UserDetails.class); // UserDetails 타입으로 캐스팅
	}

}
