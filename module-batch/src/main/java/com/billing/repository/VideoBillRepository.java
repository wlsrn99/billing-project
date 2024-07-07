package com.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.entity.VideoBill;

public interface VideoBillRepository extends JpaRepository<VideoBill, Long> {
}
