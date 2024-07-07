package com.billing.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WatchHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long videoId;

	private int lastWatchedPosition;

	// 만든 날짜 -> 배치 통계테이블 만들 때 쓰일 예정
	private LocalDate createdAt;

	// 재생 버튼을 누른 시간 -> duration 계산용
	private LocalDateTime watchedAt;

	// 동영상 재생 시간
	private int duration;

	// 오늘 광고 본 횟수
	private int adViewCount;

	@Builder
	public WatchHistory(Long userId, Long videoId, int lastWatchedPosition, LocalDate createdAt,
		LocalDateTime watchedAt,
		int duration, int adViewCount) {
		this.userId = userId;
		this.videoId = videoId;
		this.lastWatchedPosition = lastWatchedPosition;
		this.createdAt = createdAt;
		this.watchedAt = watchedAt;
		this.duration = duration;
		this.adViewCount = adViewCount;
	}

}
