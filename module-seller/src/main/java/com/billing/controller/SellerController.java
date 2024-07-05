package com.billing.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billing.dto.TotalBillDTO;
import com.billing.dto.VideoBillDTO;
import com.billing.service.BillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/seller/api")
@RequiredArgsConstructor
public class SellerController {
	private final BillService billService;

	@GetMapping("/view/{videoId}")
	public ResponseEntity<ResponseMessage<VideoBillDTO>> readViewBill(
		@PathVariable Long videoId,
		@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		VideoBillDTO videoBillDTO = billService.readViewBill(videoId, startDate, endDate);

		ResponseMessage<VideoBillDTO> responseMessage = ResponseMessage.<VideoBillDTO>builder()
			.message(startDate + "부터 " + endDate + "까지의 조회수 정산 비용입니다")
			.data(videoBillDTO)
			.build();

		return ResponseEntity.ok(responseMessage);
	}

	@GetMapping("/ad/{videoId}")
	public ResponseEntity<ResponseMessage<VideoBillDTO>> readAdBill(
		@PathVariable Long videoId,
		@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		VideoBillDTO videoBillDTO = billService.readAdBill(videoId, startDate, endDate);

		ResponseMessage<VideoBillDTO> responseMessage = ResponseMessage.<VideoBillDTO>builder()
			.message(startDate + "부터 " + endDate + "까지의 광고 조회수 정산 비용입니다")
			.data(videoBillDTO)
			.build();

		return ResponseEntity.ok(responseMessage);
	}

	@GetMapping("/total/{videoId}")
	public ResponseEntity<ResponseMessage<TotalBillDTO>> readTotalBill(
		@PathVariable Long videoId,
		@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
		@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		TotalBillDTO totalBillDTO = billService.readTotalBill(videoId, startDate, endDate);

		ResponseMessage<TotalBillDTO> responseMessage = ResponseMessage.<TotalBillDTO>builder()
			.message(startDate + "부터 " + endDate + "까지의 총 정산 비용입니다")
			.data(totalBillDTO)
			.build();

		return ResponseEntity.ok(responseMessage);
	}
}
