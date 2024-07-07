package com.streaming.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResponseDTO {
	//동영상 제목
	//동영상 길이
	//등록된 광고의 개수
	private String title;
	private int duration;
	private int adCount;

	@Builder
	public CreateResponseDTO(String title, int duration, int adCount) {
		this.title = title;
		this.duration = duration;
		this.adCount = adCount;
	}
}
