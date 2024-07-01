package com.streaming.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.streaming.entity.Video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveResponseDTO {
	private Long id;
	private String title;
	private int duration;
	private List<VideoAdDTO> videoAds;

	//json양방향 순환 참조 문제 해결
	public SaveResponseDTO(Video video) {
		this.id = video.getId();
		this.title = video.getTitle();
		this.duration = video.getDuration();
		this.videoAds = video.getVideoAds().stream()
			.map(VideoAdDTO::new)
			.collect(Collectors.toList());
	}
}
