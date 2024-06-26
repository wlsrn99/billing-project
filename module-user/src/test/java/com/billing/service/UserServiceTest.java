package com.billing.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.billing.BillingProjectApplication;
import com.billing.user.dto.UserRequestDTO;
import com.billing.user.dto.UserResponseDTO;
import com.billing.user.entity.UserType;
import com.billing.user.repository.UserRepository;
import com.billing.user.service.UserService;

@SpringBootTest(classes = BillingProjectApplication.class)
@Transactional
public class UserServiceTest {
	@Autowired
	UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// @BeforeEach
	// void setUp(){
	// 	User user = User.builder()
	// 		.email("email@email.com")
	// 		.password(passwordEncoder.encode("@@1234123password"))
	// 		.userType(UserType.UNVERIFIED)
	// 		.build();
	// }

	@Test
	@DisplayName("유저 생성 성공 테스트")
	void createUser() {
		//given
		UserRequestDTO requestDTO = new UserRequestDTO();
		requestDTO.setEmail("email@gmail.com");
		requestDTO.setPassword("@@12312312test");
		requestDTO.setUsertype(UserType.USER.toString());

		//when
		UserResponseDTO responseDTO = userService.createUser(requestDTO);

		//then
		String findEmail = userRepository.findByEmail(responseDTO.getEmail()).get().getEmail();
		assertThat(findEmail).isEqualTo(requestDTO.getEmail());
	}

}
