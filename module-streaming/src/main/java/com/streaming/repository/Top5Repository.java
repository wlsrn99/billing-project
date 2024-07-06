package com.streaming.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.streaming.dto.PeriodType;
import com.streaming.dto.bill.QTopDurationVideoDTO;
import com.streaming.dto.bill.QTopViewedVideoDTO;
import com.streaming.dto.bill.TopDurationVideoDTO;
import com.streaming.dto.bill.TopViewedVideoDTO;
import com.streaming.entity.QVideo;
import com.streaming.entity.QVideoStatistic;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class Top5Repository {
	private final JPAQueryFactory queryFactory;

	public List<TopViewedVideoDTO> getTopViewedVideos(LocalDate date, PeriodType periodType, long userId) {
		QVideoStatistic videoStatistic = QVideoStatistic.videoStatistic;
		QVideo video = QVideo.video;

		return queryFactory
			.select(new QTopViewedVideoDTO(
				video.id.as("videoId"),
				video.title,
				videoStatistic.dailyViewCount.sum().as("totalViewCount")
			))
			.from(videoStatistic)
			.join(video).on(videoStatistic.videoId.eq(video.id))
			.where(dateCondition(videoStatistic.date, date, periodType))
			.groupBy(videoStatistic.videoId, video.title)
			.orderBy(videoStatistic.dailyViewCount.sum().desc())
			.limit(5)
			.fetch();
	}

	public List<TopDurationVideoDTO> getTopDurationVideos(LocalDate date, PeriodType periodType, long userId) {
		QVideoStatistic videoStatistic = QVideoStatistic.videoStatistic;
		QVideo video = QVideo.video;

		return queryFactory
			.select(new QTopDurationVideoDTO(
				video.id.as("videoId"),
				video.title,
				videoStatistic.dailyDuration.sum().as("totalDuration")
			))
			.from(videoStatistic)
			.join(video).on(videoStatistic.videoId.eq(video.id))
			.where(dateCondition(videoStatistic.date, date, periodType))
			.groupBy(videoStatistic.videoId, video.title)
			.orderBy(videoStatistic.dailyDuration.sum().desc())
			.limit(5)
			.fetch();
	}

	private BooleanExpression dateCondition(DatePath<LocalDate> datePath, LocalDate inputDate, PeriodType periodType) {
		switch (periodType) {
			case DAILY:
				return datePath.eq(inputDate);
			case WEEKLY:
				LocalDate startOfWeek = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
				LocalDate endOfWeek = startOfWeek.plusDays(6);
				return datePath.between(startOfWeek, endOfWeek);
			case MONTHLY:
				LocalDate startOfMonth = inputDate.withDayOfMonth(1);
				LocalDate endOfMonth = inputDate.with(TemporalAdjusters.lastDayOfMonth());
				return datePath.between(startOfMonth, endOfMonth);
			default:
				throw new IllegalArgumentException("Invalid period type");
		}
	}
}
