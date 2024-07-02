package com.streaming.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.DailyVideo;

public interface DailyVideoRepository extends JpaRepository<DailyVideo, Long> {
	Optional<DailyVideo> findByVideoIdAndDate(long videoId, LocalDate date);
}
