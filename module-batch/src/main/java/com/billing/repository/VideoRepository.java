package com.billing.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VideoRepository {

	private final JdbcTemplate jdbcTemplate;

	public Long findViewCountById(Long id) {
		String sql = "SELECT view_count FROM videos WHERE videos.video_id = ?";
		return jdbcTemplate.queryForObject(sql, Long.class, id);
	}

	public Long findAdCountById(Long id) {
		String sql = "SELECT ad_count FROM videos WHERE videos.video_id = ?";
		return jdbcTemplate.queryForObject(sql, Long.class, id);
	}
}

