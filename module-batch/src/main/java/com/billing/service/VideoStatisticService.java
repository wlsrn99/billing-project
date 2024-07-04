package com.billing.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billing.dto.VideoTop5DurationDTO;
import com.billing.dto.VideoTop5ViewDTO;
import com.billing.repository.VideoStatisticRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoStatisticService {
	private final VideoStatisticRepository videoStatisticRepository;

	public List<VideoTop5ViewDTO> getTop5DailyViewCount(LocalDate date) {
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByDailyViewCount(date, pageable);
	}

	public List<VideoTop5ViewDTO> getTop5WeeklyViewCount(LocalDate date) {
		LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
		LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByWeeklyViewCount(startOfWeek, endOfWeek, pageable);
	}

	public List<VideoTop5ViewDTO> getTop5MonthlyViewCount(LocalDate date) {
		LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByMonthlyViewCount(startOfMonth, endOfMonth, pageable);
	}

	public List<VideoTop5DurationDTO> getTop5DailyDuration(LocalDate date) {
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByDailyDuration(date, pageable);
	}

	public List<VideoTop5DurationDTO> getTop5WeeklyDuration(LocalDate date) {
		LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
		LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByWeeklyDuration(startOfWeek, endOfWeek, pageable);
	}

	public List<VideoTop5DurationDTO> getTop5MonthlyDuration(LocalDate date) {
		LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
		Pageable pageable = PageRequest.of(0, 5);
		return videoStatisticRepository.findTop5ByMonthlyDuration(startOfMonth, endOfMonth, pageable);
	}

	// 현재 시간을 검증하고 통계, 정산 데이터가 아직 없는 상태라면 이전일 기준으로 통계 낼때 쓰이는 메서드
	// private LocalDate varifyCrrentTime(LocalDate date) {
	// 	LocalDateTime now = LocalDateTime.now();
	// 	if (now.getHour() < 6) {
	// 		return date.minusDays(1);
	// 	}
	// 	return date;
	// }
}
