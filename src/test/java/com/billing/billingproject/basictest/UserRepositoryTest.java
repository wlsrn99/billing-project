package com.billing.billingproject.basictest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.billing.billingproject.user.entity.User;
import com.billing.billingproject.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class UserRepositoryTest {
	@Autowired
	EntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Test
	public void basicTest() throws Exception {
		// given
		User user = new User("wlsrn@naver.com");
		userRepository.save(user);
		// when
		User findUser = userRepository.findById(user.getId()).get();
		// then
		Assertions.assertThat(findUser).isEqualTo(user);
	}
}
