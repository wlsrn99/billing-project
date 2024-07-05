package com.streaming.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@OneToMany(mappedBy = "video", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<VideoAd> videoAds = new ArrayList<>();

	@Builder
	public Video(Long userId, String title, int duration, long viewCount, long adCount, List<VideoAd> videoAds
	, LocalDateTime createdAt) {
		this.userId = userId;
		this.title = title;
		this.duration = duration;
		this.viewCount = viewCount;
		this.adCount = adCount;
		this.videoAds = videoAds;
		this.createdAt = createdAt;
	}

	public void increaseViewCount(long viewCount){
		this.viewCount = viewCount;
	}

	public void increaseAdCount(long adCount){
		this.adCount = adCount;
	}
}
