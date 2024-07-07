package com.streaming.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.streaming.dto.bill.BillVideoDTO;
import com.streaming.dto.bill.PeriodType;
import com.streaming.dto.bill.QBillVideoDTO;
import com.streaming.entity.QVideo;
import com.streaming.entity.QVideoBill;
import com.streaming.utill.DateConditionUtil;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BillRepository {
	private final JPAQueryFactory queryFactory;
	private final DateConditionUtil dateConditionUtil;

	public List<BillVideoDTO> getBillVideoById(long videoId, LocalDate date, PeriodType periodType) {
		QVideoBill videoBill = QVideoBill.videoBill;
		QVideo video = QVideo.video;
		return queryFactory.select(
				new QBillVideoDTO(
					video.id.as("videoId"),
					video.title, videoBill.dailyViewBill.sum().as("viewBill"),
					videoBill.dailyAdBill.sum().as("adBill"),
					videoBill.totalBill.sum().as("totalBill")))
			.from(videoBill)
			.join(video)
			.on(videoBill.videoId.eq(video.id))
			.where(dateConditionUtil.createDateCondition(videoBill.date, date, periodType))
			.where(videoBill.videoId.eq(videoId))
			.groupBy(videoBill.videoId)
			.fetch();
	}

}
