package com.billing.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "videos")
// extends Timestamped
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long id;

	private Long userId;

	private String title;

	private int duration;

	private Long viewCount;

	private Long adCount;

	private LocalDateTime createdAt;

	@Builder
	public Video(Long userId, String title, int duration, long viewCount, long adCount
		, LocalDateTime createdAt) {
		this.userId = userId;
		this.title = title;
		this.duration = duration;
		this.viewCount = viewCount;
		this.adCount = adCount;
		this.createdAt = createdAt;
	}

}
