package com.billing.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DailyVideoDTO {
	private String title;
	private int viewCount;

	public DailyVideoDTO(String title, int viewCount) {
		this.title = title;
		this.viewCount = viewCount;
	}
}
