package com.streaming.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.aop.annotations.ReadOnly;
import com.streaming.dto.bill.PeriodType;
import com.streaming.dto.bill.TopDurationVideoDTO;
import com.streaming.dto.bill.TopViewedVideoDTO;
import com.streaming.repository.Top5Repository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Top5Service {
	private final Top5Repository top5Repository;

	@ReadOnly
	@Transactional(readOnly = true)
	public List<TopViewedVideoDTO> readTop5ViewBill(LocalDate date, long userId, PeriodType periodType) {
		return top5Repository.getTopViewedVideos(date, periodType, userId);
	}

	@ReadOnly
	@Transactional(readOnly = true)
	public List<TopDurationVideoDTO> readTop5DurationBill(LocalDate date, long userId, PeriodType periodType) {
		return top5Repository.getTopDurationVideos(date, periodType, userId);
	}
}
