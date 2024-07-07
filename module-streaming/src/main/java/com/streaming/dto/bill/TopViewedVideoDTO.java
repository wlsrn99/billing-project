package com.streaming.dto.bill;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopViewedVideoDTO {
	private long videoId;
	private String title;
	private long totalViewCount;

	@QueryProjection
	public TopViewedVideoDTO(long videoId, String title, long totalViewCount) {
		this.videoId = videoId;
		this.title = title;
		this.totalViewCount = totalViewCount;
	}


}

