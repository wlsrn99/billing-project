package com.streaming.dto;

import com.streaming.entity.VideoAd;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAdDTO {
	private Long id;
	private int insertTime;
	private int viewCount;

	public VideoAdDTO(VideoAd videoAd) {
		this.id = videoAd.getId();
		this.insertTime = videoAd.getInsertTime();
		this.viewCount = videoAd.getViewCount();
	}
}
