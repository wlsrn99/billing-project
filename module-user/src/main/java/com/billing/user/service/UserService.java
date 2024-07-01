package com.billing.user.service;


import static com.billing.exception.UserException.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billing.exception.UserErrorCode;
import com.billing.user.dto.LoginResponseDTO;
import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.entity.UserEntity;
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

		UserEntity userEntity = UserEntity.builder()
			.password(password)
			.email(requestDTO.getEmail())
			.userType(userType)
			.build();

		UserEntity saveUserEntity = userRepository.save(userEntity);

		return new UserResponseDTO(saveUserEntity);
	}

	@Transactional
	public LoginResponseDTO login(String email, String password){
		UserEntity userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> new InvalidEmailException(UserErrorCode.INVALID_EMAIL_ERROR));

		if (!passwordEncoder.matches(password, userEntity.getPassword())) {
			throw new InvalidPasswordException(UserErrorCode.INVALID_PASSWORD_ERROR);
		}

		Long userId = userEntity.getId();
		//리프레쉬 토큰 생성, DB에 저장
		String refreshToken = jwtUtil.createRefreshToken(email, Collections.singletonList(userEntity.getUserType().getAuthority()), userId);
		userEntity.refreshTokenReset(refreshToken);
		UserEntity saveUserEntity = userRepository.save(userEntity);

		String accessToken = jwtUtil.createAccessToken(email, Collections.singletonList(userEntity.getUserType().getAuthority()), userId);
		LoginResponseDTO loginResponseDTO = new LoginResponseDTO(email,accessToken);


		return loginResponseDTO;
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

		UserEntity existingUserEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

		String refreshToken = existingUserEntity.getRefreshToken();
		existingUserEntity.refreshTokenReset("");
		userRepository.save(existingUserEntity);

		jwtUtil.invalidateToken(accessToken);
		jwtUtil.invalidateToken(refreshToken);
	}

	public String getNewToken(String email) {
		UserEntity userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("refresh UserEntity Not Found" + email));

		String newToken = jwtUtil.refreshAccessToken(userEntity.getRefreshToken());
		return newToken;
	}

	public UserDetails getUserDetails(String userEmail) {
		// 사용자 정보를 DB 에서 조회
		UserEntity userEntity = userRepository.findByEmail(userEmail).get();

		// UserDetails 객체로 변환하여 반환
		return new User(
			userEntity.getEmail(),
			userEntity.getPassword(),
			Collections.singletonList(new SimpleGrantedAuthority(userEntity.getUserType().getAuthority())));
	}


	/**
	 * 이메일 유효성 검사
	 * @param email 이메일
	 */
	private void validateUserEmail(String email) {
		Optional<UserEntity> findUser = userRepository.findByEmail(email);
		if(findUser.isPresent()) {
			throw new EmailDuplicatedException(UserErrorCode.EMAIL_DUPLICATED);
		}
	}


}