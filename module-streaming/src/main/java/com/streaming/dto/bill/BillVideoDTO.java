package com.streaming.dto.bill;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillVideoDTO {
	private long videoId;
	private String title;
	private long viewBill;
	private long adBill;
	private long totalBill;

	@QueryProjection
	public BillVideoDTO(long videoId, String title, long viewBill, long adBill, long totalBill) {
		this.videoId = videoId;
		this.title = title;
		this.viewBill = viewBill;
		this.adBill = adBill;
		this.totalBill = totalBill;
	}
}
