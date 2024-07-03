package com.billing.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.entity.DailyVideo;

public interface DailyVideoRepository extends JpaRepository<DailyVideo, Long> {
	List<DailyVideo> findTop5ByDateOrderByViewCountDesc(LocalDate date);

	List<DailyVideo> findByDateEquals(LocalDate now);

	List<DailyVideo> findAllByDateBetweenAndVideoId(LocalDate startOfWeek, LocalDate endOfWeek, Long id);
}
