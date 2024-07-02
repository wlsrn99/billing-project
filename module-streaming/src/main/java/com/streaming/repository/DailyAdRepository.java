package com.streaming.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.DailyAd;
import com.streaming.entity.Video;

public interface DailyAdRepository extends JpaRepository<DailyAd, Long> {
	Optional<DailyAd> findByVideoAndDate(Video video, LocalDate date);
}
