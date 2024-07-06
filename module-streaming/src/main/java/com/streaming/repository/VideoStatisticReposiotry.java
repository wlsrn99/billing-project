package com.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.VideoStatistic;

public interface VideoStatisticReposiotry extends JpaRepository<VideoStatistic, Long> {
}
