package com.streaming.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
public class Video extends Timestamped{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long id;

	private Long userId;

	private String title;

	private int duration;

	private int viewCount;

	@OneToMany(mappedBy = "video", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<VideoAd> videoAds = new ArrayList<>();

	@Builder
	public Video(Long userId, String title, int duration, int viewCount) {
		this.userId = userId;
		this.title = title;
		this.duration = duration;
		this.viewCount = viewCount;
	}

	public void increaseViewCount(int viewCount){
		this.viewCount = viewCount;
	}
}
