package com.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.entity.VideoStatistic;

public interface VideoStatisticRepository extends JpaRepository<VideoStatistic, Long> {
}
