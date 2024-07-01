package com.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

}
