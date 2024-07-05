package com.billing.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.billing.dto.TotalBillDTO;
import com.billing.dto.VideoBillDTO;
import com.billing.repository.VideoStatisticRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

	private final VideoStatisticRepository videoStatisticRepository;

	public VideoBillDTO readViewBill(Long videoId, LocalDate startDate, LocalDate endDate) {
		return videoStatisticRepository.findViewBillByVideo(startDate, endDate, videoId);
	}

	public VideoBillDTO readAdBill(Long videoId, LocalDate startDate, LocalDate endDate) {
		return videoStatisticRepository.findAdBillByVideo(startDate, endDate, videoId);
	}

	public TotalBillDTO readTotalBill(Long videoId, LocalDate startDate, LocalDate endDate) {
		return videoStatisticRepository.findTotalBillByVideo(startDate, endDate, videoId);
	}
}
