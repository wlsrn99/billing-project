package com.billing.billingproject.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.billingproject.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByEmail(String email);
}
