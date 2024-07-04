package com.billing.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billing.dto.Top5DurationResponse;
import com.billing.dto.VideoTop5DurationDTO;
import com.billing.dto.VideoTop5ViewDTO;
import com.billing.dto.Top5ViewResponse;
import com.billing.service.VideoStatisticService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/batch/api")
@RequiredArgsConstructor
public class BatchController {
	private final VideoStatisticService videoStatisticService;

	@GetMapping("/top5/view")
	public ResponseEntity<Top5ViewResponse> getViewCounts(@RequestParam LocalDate date) {
		List<VideoTop5ViewDTO> dailyTop5 = videoStatisticService.getTop5DailyViewCount(date);
		List<VideoTop5ViewDTO> weeklyTop5 = videoStatisticService.getTop5WeeklyViewCount(date);
		List<VideoTop5ViewDTO> monthlyTop5 = videoStatisticService.getTop5MonthlyViewCount(date);

		Top5ViewResponse response = new Top5ViewResponse(dailyTop5, weeklyTop5, monthlyTop5);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/top5/duration")
	public ResponseEntity<Top5DurationResponse> getDurations(@RequestParam LocalDate date) {
		List<VideoTop5DurationDTO> dailyTop5 = videoStatisticService.getTop5DailyDuration(date);
		List<VideoTop5DurationDTO> weeklyTop5 = videoStatisticService.getTop5WeeklyDuration(date);
		List<VideoTop5DurationDTO> monthlyTop5 = videoStatisticService.getTop5MonthlyDuration(date);

		Top5DurationResponse response = new Top5DurationResponse(dailyTop5, weeklyTop5, monthlyTop5);

		return ResponseEntity.ok(response);
	}
}

