package com.billing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoBillDTO {
	private Long videoId;
	private String title;
	private long totalBill = 0;
	private long viewCount = 0;

	public VideoBillDTO(Long videoId, String title, long totalBill, long viewCount) {
		this.videoId = videoId;
		this.title = title;
		this.totalBill = totalBill;
		this.viewCount = viewCount;
	}
}
