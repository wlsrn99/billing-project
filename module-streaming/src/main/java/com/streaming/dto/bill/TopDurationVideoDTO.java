package com.streaming.dto.bill;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopDurationVideoDTO {
	private long videoId;
	private String title;
	private long totalDuration;

	@QueryProjection
	public TopDurationVideoDTO(long videoId, String title, long totalDuration) {
		this.videoId = videoId;
		this.title = title;
		this.totalDuration = totalDuration;
	}
}
