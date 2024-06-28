package com.billing.user.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/api")
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
		LoginResponseDTO responseDTO = userService.login(requestDTO.getEmail(), requestDTO.getPassword());

		String accessToken = jwtUtil.createAccessToken(requestDTO.getEmail());
		HttpHeaders headers = new HttpHeaders();
		headers.add(jwtUtil.AUTHORIZATION_HEADER, accessToken);

		ResponseMessage<LoginResponseDTO> responseMessage = ResponseMessage.<LoginResponseDTO>builder()
			.statusCode(HttpStatus.OK.value())
			.message("로그인이 완료되었습니다.")
			.data(responseDTO)
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
	@GetMapping("/logout")
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

	// 토큰을 통해 id 제공 - 다른 서비스에서 요청할 데이터
	@PostMapping("/refresh")
	public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String oldToken) {
		try {
			return userService.getNewToken(oldToken);
		} catch (Exception e) {
			// 갱신 실패 시 에러 응답
			ResponseMessage<String> errorMessage = ResponseMessage.<String>builder()
				.statusCode(HttpStatus.OK.value())
				.message("토큰 갱신에 실패 하였습니다")
				.build();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
		}
	}



}
