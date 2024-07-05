package com.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
