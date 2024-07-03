package com.billing.processor;

import org.springframework.batch.item.ItemProcessor;

import com.billing.entity.DailyVideo;
import com.billing.entity.VideoStatistic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DailyStatisticsProcessor implements ItemProcessor<DailyVideo, VideoStatistic> {
	@Override
	public VideoStatistic process(DailyVideo item) throws Exception {
		log.info("DailyStatisticsProcessor process start");
		return VideoStatistic.builder()
			.video(item.getVideo())
			.date(item.getDate())
			.dailyViewCount(item.getViewCount())
			.weeklyViewCount(0)  // 초기화, 주간 프로세서에서 설정
			.monthlyViewCount(0) // 초기화, 월간 프로세서에서 설정
			.build();
	}
}
