package com.billing.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_ads")
@Getter
public class DailyAd {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "daily_ad_view")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "vide_id")
	private Video video;

	private LocalDate date;

	private int viewCount;

	@Builder
	public DailyAd(Video video, LocalDate date, int viewCount) {
		this.video = video;
		this.date = date;
		this.viewCount = viewCount;
	}

	public void increaseViewCount(int count) {
		this.viewCount += count;
	}
}
