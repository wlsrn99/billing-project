package com.billing.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Top5DurationResponse {
	private List<List<VideoTop5DurationDTO>> durationTop5 = new ArrayList<>(3);

	public Top5DurationResponse(List<VideoTop5DurationDTO> dailyTop5, List<VideoTop5DurationDTO> weeklyTop5,
		List<VideoTop5DurationDTO> monthlyTop5) {
		durationTop5.add(dailyTop5);
		durationTop5.add(weeklyTop5);
		durationTop5.add(monthlyTop5);
	}
}
