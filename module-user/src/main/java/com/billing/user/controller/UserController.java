package com.billing.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billing.user.dto.LoginRequestDTO;
import com.billing.user.dto.LoginResponseDTO;
import com.billing.user.dto.ResponseMessage;
import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.jwt.JwtUtil;
import com.billing.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/api")
@Slf4j
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;

	/**
	 * 1. 회원 가입
	 * @param requestDTO 회원 가입 요청 데이터
	 * @return ResponseEntity<ResponseMessage<UserResponseDTO>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 회원 가입이 성공적으로 이루어지면 201 (CREATED)
	 * 	   - 메시지: 회원 가입 상태를 설명하는 메시지
	 * 	   - 데이터: 생성된 회원의 정보를 담고 있는 UserResponseDTO 객체
	 */
	@PostMapping("/signup")
	public ResponseEntity<ResponseMessage<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
		UserResponseDTO responseDTO = userService.createUser(requestDTO);

		ResponseMessage<UserResponseDTO> responseMessage = ResponseMessage.<UserResponseDTO>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("회원가입이 완료되었습니다.")
			.data(responseDTO)
			.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseMessage<LoginResponseDTO>> loginUser(@RequestBody LoginRequestDTO requestDTO) {
		LoginResponseDTO response = userService.login(requestDTO.getEmail(), requestDTO.getPassword());

		String accessToken = response.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.add(jwtUtil.AUTHORIZATION_HEADER, accessToken);

		ResponseMessage<LoginResponseDTO> responseMessage = ResponseMessage.<LoginResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("로그인이 완료되었습니다.")
			.data(response)
			.build();

		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseMessage);
	}



	/**
	 * 3. 로그아웃
	 * @param request HTTP 요청
	 * @return ResponseEntity<ResponseMessage<String>> 형태의 HTTP 응답. 이 응답은 다음을 포함한다:
	 * 	   - 상태 코드: 로그아웃이 성공적으로 이루어지면 200 (OK)
	 * 	   - 메시지: 로그아웃 상태를 설명하는 메시지
	 * 	   - 데이터: 로그아웃된 회원의 이메일
	 */
	@PostMapping("/logout")
	public ResponseEntity<ResponseMessage<String>> logout(HttpServletRequest request){

		String accessToken = jwtUtil.getJwtFromHeader(request);
		String email = jwtUtil.getEmailFromToken(accessToken);

		userService.logout(email, accessToken);

		ResponseMessage<String> responseMessage = ResponseMessage.<String>builder()
			.statusCode(HttpStatus.OK.value())
			.message("로그아웃이 완료되었습니다.")
			.data(email)
			.build();

		return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
	}

	// 이용자 email을 통해 새로운 access 토큰 제공 - 다른 모듈 서비스에서 요청할 데이터
	@PostMapping("/refresh")
	public String getNewAccessToken(@RequestHeader("email") String email) {
		try {
			log.info("oldToken: {}", email);
			String newToken = userService.getNewToken(email);
			log.error("신규 발급 토큰 : {}", newToken);
			return newToken; // 갱신된 토큰을 반환.
		}
		catch (ExpiredJwtException e) {
			return "expired token";
		}
		catch (JwtException | IllegalArgumentException e) {
			// 갱신 실패 시 에러 응답
			return "Token refresh failed!!!";
		}
	}

	@GetMapping("/{userEmail}")
	public UserDetails getUserInfoByUserId(@PathVariable String userEmail) {
		return userService.getUserDetails(userEmail);
	}



}
