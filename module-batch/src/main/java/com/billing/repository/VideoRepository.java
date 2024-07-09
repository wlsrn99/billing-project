package com.billing.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
	@Query("SELECT v.id, v.viewCount, v.adCount FROM Video v")
	List<Object[]> findAllViewAndAdCountsPaged(Pageable pageable);

	@Query("SELECT v.viewCount FROM Video v WHERE v.id = :id")
	Long findViewCountById(@Param("id") Long id);

	@Query("SELECT v.adCount FROM Video v WHERE v.id = :id")
	Long findAdCountById(@Param("id") Long id);
}

