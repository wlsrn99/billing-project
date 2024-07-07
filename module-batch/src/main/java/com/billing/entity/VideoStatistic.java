package com.billing.entity;

import java.time.LocalDate;

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
@Table(name = "video_statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VideoStatistic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long videoId;

	private LocalDate date;

	private long dailyViewCount;

	private long dailyAdViewCount;

	private long dailyDuration;

	@Builder
	public VideoStatistic(Long videoId, LocalDate date, long dailyViewCount, long dailyAdViewCount,
		long dailyDuration) {
		this.videoId = videoId;
		this.date = date;
		this.dailyViewCount = dailyViewCount;
		this.dailyAdViewCount = dailyAdViewCount;
		this.dailyDuration = dailyDuration;
	}

}
