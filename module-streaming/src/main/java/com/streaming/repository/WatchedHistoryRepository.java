package com.streaming.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.streaming.entity.WatcheHistory;

public interface WatchedHistoryRepository extends JpaRepository<WatcheHistory, Long> {
	Optional<WatcheHistory> findByUserIdAndVideoId(Long userId, Long videoId);
}
