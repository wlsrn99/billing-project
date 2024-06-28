package com.billing.user.service;


import static com.billing.exception.UserException.*;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billing.exception.UserErrorCode;
import com.billing.user.dto.LoginResponseDTO;
import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.entity.User;
import com.billing.user.entity.UserType;
import com.billing.user.jwt.JwtUtil;
import com.billing.user.repository.UserRepository;

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

		UserType userType = requestDTO.getUsertype().equals("시청자") ? UserType.USER: UserType.SELEER;


		//비밀번호 암호화
		String password = passwordEncoder.encode(requestDTO.getPassword());

		User user = User.builder()
			.password(password)
			.email(requestDTO.getEmail())
			.userType(userType)
			.build();

		User saveUser = userRepository.save(user);

		return new UserResponseDTO(saveUser);
	}

	@Transactional
	public LoginResponseDTO login(String email, String password){
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new InvalidEmailException(UserErrorCode.INVALID_EMAIL_ERROR));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException(UserErrorCode.INVALID_PASSWORD_ERROR);
		}

		//리프레쉬 토큰 생성
		String refreshToken = jwtUtil.createRefreshToken(email);
		user.refreshTokenReset(refreshToken);
		User saveUser = userRepository.save(user);

		return new LoginResponseDTO(saveUser);
	}

	/**
	 * 3. 로그아웃
	 * @param email 로그인한 사용자의 이메일
	 * @param accessToken access token
	 */
	@Transactional
	public void logout(String email, String accessToken) {

		if(email == null){
			throw new UserUnauthorizedException(UserErrorCode.UNAUTHORIZED_ACCESS_ERROR);
		}

		User existingUser = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

		String refreshToken = existingUser.getRefreshToken();
		existingUser.refreshTokenReset("");
		userRepository.save(existingUser);

		jwtUtil.invalidateToken(accessToken);
		jwtUtil.invalidateToken(refreshToken);
	}

	public ResponseEntity<String> getNewToken(String oldToken) {
		// 토큰 갱신 로직을 호출
		String email = jwtUtil.getEmailFromToken(oldToken);
		User user  = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
		String newToken = jwtUtil.refreshAccessToken(user.getRefreshToken());
		return ResponseEntity.ok(newToken);
	}


	/**
	 * 이메일 유효성 검사
	 * @param email 이메일
	 */
	private void validateUserEmail(String email) {
		Optional<User> findUser = userRepository.findByEmail(email);
		if(findUser.isPresent()) {
			throw new EmailDuplicatedException(UserErrorCode.EMAIL_DUPLICATED);
		}
	}


}