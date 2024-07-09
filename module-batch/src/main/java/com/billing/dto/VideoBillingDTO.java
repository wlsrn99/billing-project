package com.billing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoBillingDTO {
	private Long videoId;
	private Long viewCount;
	private Long adCount;

	public VideoBillingDTO(Long videoId, Long viewCount, Long adCount) {
		this.videoId = videoId;
		this.viewCount = viewCount;
		this.adCount = adCount;
	}
}
