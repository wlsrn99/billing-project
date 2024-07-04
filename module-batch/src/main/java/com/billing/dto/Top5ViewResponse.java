package com.billing.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Top5ViewResponse {
	private List<List<VideoTop5ViewDTO>> viewTop5 = new ArrayList<>(3);

	public Top5ViewResponse(List<VideoTop5ViewDTO> dailyTop5, List<VideoTop5ViewDTO> weeklyTop5,
		List<VideoTop5ViewDTO> monthlyTop5) {
		viewTop5.add(dailyTop5);
		viewTop5.add(weeklyTop5);
		viewTop5.add(monthlyTop5);
	}
}