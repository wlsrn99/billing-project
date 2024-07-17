package com.streaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.streaming.entity.Video;

import jakarta.persistence.LockModeType;

public interface VideoRepository extends JpaRepository<Video, Long> {
	@Modifying
	@Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE) //비관적 락
	void incrementViewCount(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Video v SET v.adCount = v.adCount + :count WHERE v.id = :id")
	void incrementAdCount(@Param("id") Long id, @Param("count") int count);
}
