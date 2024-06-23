package com.billing.billingproject.user.service;


import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billing.billingproject.user.dto.UserRequestDTO;
import com.billing.billingproject.user.dto.UserResponseDTO;
import com.billing.billingproject.user.entity.User;
import com.billing.billingproject.user.entity.UserType;
import com.billing.billingproject.user.exception.UserException;
import com.billing.billingproject.user.jwt.JwtUtil;
import com.billing.billingproject.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	/**
	 * 1. 회원 가입
	 * @param requestDTO 회원 가입 요청 데이터
	 * @return UserResponseDTO 회원 가입 결과
	 */
	@Transactional
	public UserResponseDTO createUser(UserRequestDTO requestDTO) {

		//이메일 유효성 검사
		validateUserEmail(requestDTO.getEmail());

		//비밀번호 암호화
		String password = passwordEncoder.encode(requestDTO.getPassword());

		User user = User.builder()
			.password(password)
			.email(requestDTO.getEmail())
			.userType(UserType.UNVERIFIED)
			.statusChangedAt(LocalDateTime.now())
			.build();

		User saveUser = userRepository.save(user);

		return new UserResponseDTO(saveUser);
	}

	/**
	 * 2. 회원 활성화
	 * @param user 활성화할 회원
	 */
	@Transactional
	public void updateUserActive(User user) {
		user.ActiveUser();
		userRepository.save(user);
	}


	/**
	 * 4. 로그아웃
	 * @param user 로그인한 사용자의 세부 정보
	 * @param accessToken access token
	 * @param refreshToken refresh token
	 */
	@Transactional
	public void logout(User user, String accessToken, String refreshToken) {

		if(user == null){
			throw new UserException("로그인되어 있는 유저가 아닙니다.");
		}

		// checkUserType(user.getUserType());

		User existingUser = userRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new UserException("해당 유저가 존재하지 않습니다."));

		existingUser.refreshTokenReset("");
		userRepository.save(existingUser);

		jwtUtil.invalidateToken(accessToken);
		jwtUtil.invalidateToken(refreshToken);
	}

	/**
	 * 이메일 유효성 검사
	 * @param email 이메일
	 */
	private void validateUserId(String email) {
		Optional<User> findUser = userRepository.findByEmail(email);
		if (findUser.isPresent()) {
			throw new UserException("중복된 이메일 입니다.");
		}
	}

	/**
	 * 이메일 유효성 검사
	 * @param email 이메일
	 */
	private void validateUserEmail(String email) {
		Optional<User> findUser = userRepository.findByEmail(email);
		if(findUser.isPresent()) {
			throw new UserException("중복된 Email 입니다.");
		}
	}

	// private void checkUserType(UserType userType) {
	// 	if (userType.equals(UserType.DEACTIVATED)) {
	// 		throw new UserException("이미 탈퇴한 회원입니다.");
	// 	}
	// }

}