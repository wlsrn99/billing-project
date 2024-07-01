package com.streaming.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class WatcheHistory extends Timestamped{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private Long videoId;

	private int lastWatchedPosition;

	private LocalDateTime lastPlayTime;

	@Builder
	public WatcheHistory(Long userId, Long videoId, int lastWatchedPosition, LocalDateTime lastPlayTime) {
		this.userId = userId;
		this.videoId = videoId;
		this.lastWatchedPosition = lastWatchedPosition;
		this.lastPlayTime = lastPlayTime;
	}

	// lastWatchedPoisition 업데이트하는 메서드
	public void updateWatchedPosition(int additionalTime) {
		this.lastWatchedPosition += additionalTime;
	}

	public void resetWatchedPosition(int duration){
		this.lastWatchedPosition = duration;
	}

	// lastPlayTime 초기화
	public void clearLastPlayTime(LocalDateTime now) {
		this.lastPlayTime = now;
	}
}
