package com.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
	@Query("SELECT v.viewCount FROM Video v WHERE v.id = :id")
	long findViewCountById(@Param("id") Long id);

	@Query("SELECT v.adCount FROM Video v WHERE v.id = :id")
	long findAdCountById(@Param("id") Long videoId);
}

