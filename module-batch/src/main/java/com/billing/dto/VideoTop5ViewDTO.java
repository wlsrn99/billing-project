package com.billing.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoTop5ViewDTO {
	private String title;
	private int viewCount;

	@Builder
	public VideoTop5ViewDTO(String title, int viewCount) {
		this.title = title;
		this.viewCount = viewCount;
	}
}

