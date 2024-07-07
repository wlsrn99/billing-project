package com.streaming.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.dto.bill.PeriodType;
import com.streaming.dto.bill.BillResponseMessage;
import com.streaming.dto.bill.TopDurationVideoDTO;
import com.streaming.dto.bill.TopViewedVideoDTO;
import com.streaming.service.Top5Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/top5/api/")
@RequiredArgsConstructor
public class Top5Controller {
	private final Top5Service top5Service;

	@GetMapping("/daily/view")
	public ResponseEntity<BillResponseMessage<List<TopViewedVideoDTO>>> readDailyTop5ViewBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	) {
		List<TopViewedVideoDTO> response = top5Service.readTop5ViewBill(date, userId, PeriodType.DAILY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopViewedVideoDTO>>builder()
			.message(userId + "님의 일일 영상 조회수 Top5 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/weekly/view")
	public ResponseEntity<BillResponseMessage<List<TopViewedVideoDTO>>>readWeeklyTop5ViewBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	){
		List<TopViewedVideoDTO> response = top5Service.readTop5ViewBill(date, userId, PeriodType.WEEKLY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopViewedVideoDTO>>builder()
			.message(userId + "님의 주간 영상 조회수 Top5 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/monthly/view")
	public ResponseEntity<BillResponseMessage<List<TopViewedVideoDTO>>>readMonthlyTop5ViewBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	){
		List<TopViewedVideoDTO> response = top5Service.readTop5ViewBill(date, userId, PeriodType.MONTHLY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopViewedVideoDTO>>builder()
			.message(userId + "님의 월간 영상 조회수 Top5 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/daily/duratin")
	public ResponseEntity<BillResponseMessage<List<TopDurationVideoDTO>>>readDailyTop5DuratinBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	){
		List<TopDurationVideoDTO> response = top5Service.readTop5DurationBill(date, userId, PeriodType.DAILY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopDurationVideoDTO>>builder()
			.message(userId + "님의 일간 영상 재생 시간 Top5 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/weekly/duratin")
	public ResponseEntity<BillResponseMessage<List<TopDurationVideoDTO>>>readWeeklyTop5DuratinBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	){
		List<TopDurationVideoDTO> response = top5Service.readTop5DurationBill(date, userId, PeriodType.WEEKLY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopDurationVideoDTO>>builder()
			.message(userId + "님의 주간 영상 재생 시간 Top5 입니다")
			.data(response)
			.build());
	}

	@GetMapping("/monthly/duratin")
	public ResponseEntity<BillResponseMessage<List<TopDurationVideoDTO>>>readMonthlyTop5DuratinBill(
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestHeader("userId") long userId
	){
		List<TopDurationVideoDTO> response = top5Service.readTop5DurationBill(date, userId, PeriodType.MONTHLY);
		return ResponseEntity.ok(BillResponseMessage.<List<TopDurationVideoDTO>>builder()
			.message(userId + "님의 월간 영상 재생 시간 Top5 입니다")
			.data(response)
			.build());
	}

}
