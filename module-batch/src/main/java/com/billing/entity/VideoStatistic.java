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

	private long dailyViewCount;
	private long weeklyViewCount;
	private long monthlyViewCount;

	private long dailyAdViewCount;
	private long weeklyAdViewCount;
	private long monthlyAdViewCount;

	private long dailyDuration;
	private long weeklyDuration;
	private long monthlyDuration;

	//정산 데이터 추가

	//영상별 정산 금액 -> 조회수
	private long dailyViewBill;
	private long weeklyViewBill;
	private long monthlyViewBill;

	//광고별 정산 금액 -> 광고 조회 수
	private long dailyAdBill;
	private long weeklyAdBill;
	private long monthlyAdBill;

	private long totalBill;

	@Builder
	public VideoStatistic(Video video, LocalDate date, long dailyViewCount, long weeklyViewCount, long monthlyViewCount,
		long dailyAdViewCount, long weeklyAdViewCount, long monthlyAdViewCount, long dailyDuration, long weeklyDuration,
		long monthlyDuration, long dailyViewBill, long weeklyViewBill, long monthlyViewBill, long dailyAdBill,
		long weeklyAdBill, long monthlyAdBill, long totalBill) {
		this.video = video;
		this.date = date;
		this.dailyViewCount = dailyViewCount;
		this.weeklyViewCount = weeklyViewCount;
		this.monthlyViewCount = monthlyViewCount;
		this.dailyAdViewCount = dailyAdViewCount;
		this.weeklyAdViewCount = weeklyAdViewCount;
		this.monthlyAdViewCount = monthlyAdViewCount;
		this.dailyDuration = dailyDuration;
		this.weeklyDuration = weeklyDuration;
		this.monthlyDuration = monthlyDuration;
		this.dailyViewBill = dailyViewBill;
		this.weeklyViewBill = weeklyViewBill;
		this.monthlyViewBill = monthlyViewBill;
		this.dailyAdBill = dailyAdBill;
		this.weeklyAdBill = weeklyAdBill;
		this.monthlyAdBill = monthlyAdBill;
		this.totalBill = totalBill;
	}
}

