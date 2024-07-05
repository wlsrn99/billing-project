package com.billing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalBillDTO {
	private Long videoId;
	private String title;
	private long totalBill = 0;
	private long viewCount = 0;
	private long adViewCount = 0;

	public TotalBillDTO(Long videoId, String title, long totalBill, long viewCount, long adViewCount) {
		this.videoId = videoId;
		this.title = title;
		this.totalBill = totalBill;
		this.viewCount = viewCount;
		this.adViewCount = adViewCount;
	}
}
