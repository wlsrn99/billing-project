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

	//영상별 정산 금액 -> 조회수
	private long dailyViewBill;

	//광고별 정산 금액 -> 광고 조회 수
	private long dailyAdBill;

	private long totalBill;

	@Builder
	public VideoStatistic(Long videoId, LocalDate date, long dailyViewCount, long dailyAdViewCount, long dailyDuration,
		long dailyViewBill, long dailyAdBill, long totalBill) {
		this.videoId = videoId;
		this.date = date;
		this.dailyViewCount = dailyViewCount;
		this.dailyAdViewCount = dailyAdViewCount;
		this.dailyDuration = dailyDuration;
		this.dailyViewBill = dailyViewBill;
		this.dailyAdBill = dailyAdBill;
		this.totalBill = totalBill;
	}


}
