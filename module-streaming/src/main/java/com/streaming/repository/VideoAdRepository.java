package com.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.VideoAd;

public interface VideoAdRepository extends JpaRepository<VideoAd, Long> {
}
