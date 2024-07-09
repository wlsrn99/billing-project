package com.streaming.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.aop.annotations.ReadOnly;
import com.streaming.dto.bill.BillVideoDTO;
import com.streaming.dto.bill.PeriodType;
import com.streaming.repository.BillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {
	private final BillRepository billRepository;

	@ReadOnly
	@Transactional(readOnly = true)
	public List<BillVideoDTO> readBill(long videoId, LocalDate date, PeriodType periodType){
		return billRepository.getBillVideoById(videoId,date,periodType);
	}
}
