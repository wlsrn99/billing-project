package com.streaming.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DailyVideo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "video_id")
	private Video video;

	private LocalDate date;
	private int viewCount;
	private int adViewCount;
	private int duration;

	@Builder
	public DailyVideo(Video video, LocalDate date, int viewCount, int adViewCount, int duration) {
		this.video = video;
		this.date = date;
		this.viewCount = viewCount;
		this.adViewCount = adViewCount;
		this.duration = duration;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void increaseDuration(int secondsWatched) {
		this.duration += secondsWatched;
	}
}
