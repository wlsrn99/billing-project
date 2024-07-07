package com.streaming.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestDTO {
	//등록할 동영상의 길이
	//등록할 동영상의 제목
	private String title;
	private int duration;

	@Builder
	public CreateRequestDTO(String title, int duration) {
		this.title = title;
		this.duration = duration;
	}
}
