package com.billing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.billing.exception.UserException;
import com.billing.user.dto.LoginResponseDTO;
import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.entity.UserEntity;
import com.billing.user.entity.UserType;
import com.billing.user.jwt.JwtUtil;
import com.billing.user.repository.UserRepository;
import com.billing.user.service.UserService;

@SpringBootTest(classes = UserModuleApplication.class)
@Transactional
@ActiveProfiles("test")
public class UserServiceTest {

	@Autowired
	UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private JwtUtil jwtUtil;

	private UserRequestDTO userRequestDTO;

	@BeforeEach
	void setUp() {
		userRequestDTO = new UserRequestDTO();
		userRequestDTO.setPassword("@@12312312test");
	}

	@Test
	@DisplayName("계정 생성 - 시청자")
	void createUserTestViewer() {
		// given
		userRequestDTO.setEmail("user@gmail.com");
		userRequestDTO.setUsertype("시청자");

		when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		UserResponseDTO responseDTO = userService.createUser(userRequestDTO);

		// then
		assertThat(responseDTO)
			.isNotNull()
			.satisfies(dto -> {
				assertThat(dto.getEmail()).isEqualTo("user@gmail.com");
				assertThat(dto.getUserType()).isEqualTo(UserType.USER.toString());
			});

	}

	@Test
	@DisplayName("계정 생성 - 판매자")
	void createUserTestSeller() {
		// given
		userRequestDTO.setEmail("seller@gmail.com");
		userRequestDTO.setUsertype("판매자");

		when(userRepository.findByEmail("seller@gmail.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		UserResponseDTO responseDTO = userService.createUser(userRequestDTO);

		// then
		assertThat(responseDTO)
			.isNotNull()
			.satisfies(dto -> {
				assertThat(dto.getEmail()).isEqualTo("seller@gmail.com");
				assertThat(dto.getUserType()).isEqualTo(UserType.SELLER.toString());
			});

	}

	@Test
	@DisplayName("생성 실패 - 중복 이메일")
	void createUserWithDuplicateEmailTest() {
		// given
		userRequestDTO.setEmail("test@gmail.com");
		userRequestDTO.setUsertype("시청자");
		UserEntity existingUserEntity = UserEntity.builder()
			.email("test@gmail.com")
			.password("encodedPassword")
			.userType(UserType.USER)
			.build();

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUserEntity));

		// when & then
		assertThatThrownBy(() -> userService.createUser(userRequestDTO))
			.isInstanceOf(UserException.EmailDuplicatedException.class)
			.hasMessageContaining("이미 이메일이 존재합니다.");

	}

	@Test
	@DisplayName("생성 실패 - 잘못된 이메일 형식")
	void loginFailInvalidEmailTest() {
		// given
		String email = "wrong@gmail.com";
		String password = "password123";
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userService.login(email, password))
			.isInstanceOf(UserException.InvalidEmailException.class);

	}

	@Test
	@DisplayName("로그인")
	void loginSuccessTest() {
		// given
		String email = "test@gmail.com";
		String password = "password123";
		UserEntity userEntity = UserEntity.builder()
			.email(email)
			.password("encodedPassword")
			.userType(UserType.USER)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
		when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(true);
		when(jwtUtil.createRefreshToken(eq(email), anyList(), eq(userEntity.getId()))).thenReturn("refreshToken");
		when(jwtUtil.createAccessToken(eq(email), anyList(), eq(userEntity.getId()))).thenReturn("accessToken");
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		// when
		LoginResponseDTO responseDTO = userService.login(email, password);

		// then
		assertThat(responseDTO).isNotNull();
		assertThat(responseDTO.getEmail()).isEqualTo(email);
		assertThat(responseDTO.getAccessToken()).isEqualTo("accessToken");

		// 리프레시 토큰이 사용자 엔티티에 저장되었는지 확인
		assertThat(userEntity.getRefreshToken()).isEqualTo("refreshToken");
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	void loginFailInvalidPasswordTest() {
		// given
		String email = "test@gmail.com";
		String password = "wrongpassword";
		UserEntity userEntity = UserEntity.builder()
			.email(email)
			.password("encodedPassword")
			.userType(UserType.USER)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
		when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> userService.login(email, password))
			.isInstanceOf(UserException.InvalidPasswordException.class);

	}

	@Test
	@DisplayName("로그아웃")
	void logoutTest() {
		// given
		String email = "test@gmail.com";
		String accessToken = "validAccessToken";
		String refreshToken = "validRefreshToken";

		UserEntity userEntity = UserEntity.builder()
			.email(email)
			.password("encodedPassword")
			.userType(UserType.USER)
			.refreshToken(refreshToken)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		// when
		userService.logout(email, accessToken);

		// then
		assertThat(userEntity.getRefreshToken()).isEmpty();
	}

	@Test
	@DisplayName("새 토큰 가져오기")
	void getNewTokenSuccessTest() {
		// given
		String email = "test@gmail.com";
		String oldRefreshToken = "oldRefreshToken";
		String newAccessToken = "newAccessToken";

		UserEntity userEntity = UserEntity.builder()
			.email(email)
			.refreshToken(oldRefreshToken)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
		when(jwtUtil.refreshAccessToken(oldRefreshToken)).thenReturn(newAccessToken);

		// when
		String result = userService.getNewToken(email);

		// then
		assertThat(result).isEqualTo(newAccessToken);
	}

	@Test
	@DisplayName("사용자 상세 정보 가져오기")
	void getUserDetailsSuccessTest() {
		// given
		String email = "test@gmail.com";
		UserEntity userEntity = UserEntity.builder()
			.email(email)
			.password("encodedPassword")
			.userType(UserType.USER)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

		// when
		UserDetails userDetails = userService.getUserDetails(email);

		// then
		assertThat(userDetails).isNotNull();
		assertThat(userDetails.getUsername()).isEqualTo(email);
		assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
		assertThat(userDetails.getAuthorities()).hasSize(1);
		assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
			.isEqualTo(UserType.USER.getAuthority());
	}
}