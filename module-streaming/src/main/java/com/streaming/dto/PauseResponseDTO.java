package com.streaming.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PauseResponseDTO {
	private long videoId;
	private long userId;
	private int lastWatchedPosition;
	private int duration;

	@Builder
	public PauseResponseDTO(long videoId, long userId, int lastWatchedPosition, int duration) {
		this.videoId = videoId;
		this.userId = userId;
		this.lastWatchedPosition = lastWatchedPosition;
		this.duration = duration;
	}
}
