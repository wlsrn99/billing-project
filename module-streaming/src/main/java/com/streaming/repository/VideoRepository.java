package com.streaming.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.streaming.entity.Video;


public interface VideoRepository extends JpaRepository<Video, Long> {
	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	// @Query("SELECT v FROM Video v WHERE v.id = :id")
	// Optional<Video> findByIdWithLock(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
	void incrementViewCount(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Video v SET v.adCount = v.adCount + :count WHERE v.id = :id")
	void incrementAdCount(@Param("id") Long id, @Param("count") int count);
}
