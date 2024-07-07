package com.streaming.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.dto.bill.BillResponseMessage;
import com.streaming.dto.bill.BillVideoDTO;
import com.streaming.dto.bill.PeriodType;
import com.streaming.service.BatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/batch/api/")
@RequiredArgsConstructor
public class BatchController {
	private final BatchService batchService;

	@GetMapping("/daily/bill")
	public ResponseEntity<BillResponseMessage<List<BillVideoDTO>>> readDailyVideoBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestParam("videoId") long videoId
	) {
		List<BillVideoDTO> response = batchService.readBill(videoId, date, PeriodType.DAILY);
		return ResponseEntity.ok(BillResponseMessage.<List<BillVideoDTO>>builder()
			.message(videoId + "의 일일 정산 내역 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/weekly/bill")
	public ResponseEntity<BillResponseMessage<List<BillVideoDTO>>> readWeeklyVideoBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestParam("videoId") long videoId
	) {
		List<BillVideoDTO> response = batchService.readBill(videoId, date, PeriodType.WEEKLY);
		return ResponseEntity.ok(BillResponseMessage.<List<BillVideoDTO>>builder()
			.message(videoId + "의 주간 정산 내역 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/monthly/bill")
	public ResponseEntity<BillResponseMessage<List<BillVideoDTO>>> readMonthlyVideoBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestParam("videoId") long videoId
	) {
		List<BillVideoDTO> response = batchService.readBill(videoId, date, PeriodType.MONTHLY);
		return ResponseEntity.ok(BillResponseMessage.<List<BillVideoDTO>>builder()
			.message(videoId + "의 월간 정산 내역 입니다")
			.data(response)
			.build());
	}

}
