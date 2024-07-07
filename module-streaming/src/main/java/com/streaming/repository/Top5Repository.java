package com.streaming.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.streaming.dto.bill.PeriodType;
import com.streaming.dto.bill.QTopDurationVideoDTO;
import com.streaming.dto.bill.QTopViewedVideoDTO;
import com.streaming.dto.bill.TopDurationVideoDTO;
import com.streaming.dto.bill.TopViewedVideoDTO;
import com.streaming.entity.QVideo;
import com.streaming.entity.QVideoStatistic;
import com.streaming.utill.DateConditionUtil;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class Top5Repository {
	private final JPAQueryFactory queryFactory;
	private final DateConditionUtil dateConditionUtil;

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
			.where(dateConditionUtil.createDateCondition(videoStatistic.date, date, periodType))
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
			.where(dateConditionUtil.createDateCondition(videoStatistic.date, date, periodType))
			.groupBy(videoStatistic.videoId, video.title)
			.orderBy(videoStatistic.dailyDuration.sum().desc())
			.limit(5)
			.fetch();
	}
}
