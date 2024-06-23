package com.billing.billingproject.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.billingproject.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
