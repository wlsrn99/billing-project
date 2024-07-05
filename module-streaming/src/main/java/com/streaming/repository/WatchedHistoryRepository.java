package com.streaming.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.streaming.entity.WatchHistory;

public interface WatchedHistoryRepository extends JpaRepository<WatchHistory, Long> {
	Optional<WatchHistory> findByUserIdAndVideoIdAndCreatedAt(Long userId, Long videoId, LocalDate date);

	@Query("SELECT wh FROM WatchHistory wh WHERE wh.userId = :userId AND wh.videoId = :videoId ORDER BY wh.createdAt DESC")
	Optional<WatchHistory> findByRecentHistory (@Param("userId") Long userId, @Param("videoId") Long videoId);

}
