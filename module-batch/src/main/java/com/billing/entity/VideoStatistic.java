package com.billing.entity;

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
@Table(name = "video_statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VideoStatistic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "video_id")
	private Video video;

	private LocalDate date;
	private int dailyViewCount;
	private int weeklyViewCount;
	private int monthlyViewCount;
	private int dailyDuration;
	private int weeklyDuration;
	private int monthlyDuration;

	@Builder
	public VideoStatistic(Video video, LocalDate date, int dailyViewCount, int weeklyViewCount, int monthlyViewCount,
		int dailyDuration, int weeklyDuration, int monthlyDuration) {
		this.video = video;
		this.date = date;
		this.dailyViewCount = dailyViewCount;
		this.weeklyViewCount = weeklyViewCount;
		this.monthlyViewCount = monthlyViewCount;
		this.dailyDuration = dailyDuration;
		this.weeklyDuration = weeklyDuration;
		this.monthlyDuration = monthlyDuration;
	}
}

