package com.billing.processor;

import java.util.HashMap;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;
import com.billing.entity.WatchHistory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyStatisticsProcessor implements ItemProcessor<WatchHistory, VideoStatistic> {

	private final HashMap<Long, VideoStatistic> cache = new HashMap<>();

	@Override
	public VideoStatistic process(WatchHistory item) throws Exception {

		// videoId를 통해 캐시에서 VideoStatistic 객체를 가져오거나 새로 생성
		return cache.compute(item.getId(), (videoId, existingStatistic) -> {
			if (existingStatistic == null) {
				// 캐시에 존재하지 않으면 새로운 VideoStatistic 객체 생성
				return VideoStatistic.builder()
					.videoId(item.getVideoId())
					.dailyViewCount(1L)
					.dailyAdViewCount(item.getAdViewCount())
					.dailyDuration(item.getDuration())
					.date(item.getCreatedAt())
					.build();
			} else {
				// 기존 객체가 있으면 viewCount 등 필요한 값을 업데이트
				existingStatistic.updateDailyViewCount(existingStatistic.getDailyViewCount() + 1);
				existingStatistic.updateDailyDuration(existingStatistic.getDailyDuration() + item.getDuration());
				existingStatistic.updateDailyAdViewCount(existingStatistic.getDailyAdViewCount() + item.getAdViewCount());
				return existingStatistic;
			}
		});
	}
}
