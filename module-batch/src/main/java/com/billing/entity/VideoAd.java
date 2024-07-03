package com.billing.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "video_ads")
public class VideoAd {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_ad_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "video_id", nullable = false)
	private Video video;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ad_detail_id")
	private AdDetail adDetail;

	private int insertTime; // 광고 삽입 시간 (초 단위)

	private int viewCount;

	@Builder
	public VideoAd(Video video, AdDetail adDetail, int insertTime) {
		this.video = video;
		this.adDetail = adDetail;
		this.insertTime = insertTime;
		this.viewCount = 0;
	}

	public void incrementViewCount() {
		this.viewCount++;
	}

	public boolean isAdWatchedDuring(int startTime, int endTime) {
		return startTime <= insertTime && insertTime <= endTime;
	}
}
