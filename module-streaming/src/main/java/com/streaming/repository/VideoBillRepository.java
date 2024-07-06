package com.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.VideoBill;

public interface VideoBillRepository extends JpaRepository<VideoBill, Long> {
}
