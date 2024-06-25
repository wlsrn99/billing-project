package com.billing.user.service;


import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.entity.User;
import com.billing.user.entity.UserType;
import com.billing.user.exception.UserException;
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


	/**
	 * 4. 로그아웃
	 * @param user 로그인한 사용자의 세부 정보
	 * @param accessToken access token
	 */
	@Transactional
	public void logout(User user, String accessToken) {

		if(user == null){
			throw new UserException("로그인되어 있는 유저가 아닙니다.");
		}

		// checkUserType(user.getUserType());

		User existingUser = userRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new UserException("해당 유저가 존재하지 않습니다."));

		String refreshToken = existingUser.getRefreshToken();
		existingUser.refreshTokenReset("");
		userRepository.save(existingUser);

		jwtUtil.invalidateToken(accessToken);
		jwtUtil.invalidateToken(refreshToken);
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


}