package com.billing.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoTop5DurationDTO {
	private String title;
	private int duration;

	@Builder
	public VideoTop5DurationDTO(String title, int duration) {
		this.title = title;
		this.duration = duration;
	}
}
