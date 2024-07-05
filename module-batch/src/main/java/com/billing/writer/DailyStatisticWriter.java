package com.billing.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;
import com.billing.util.GlobalSingletonCache;

@Component
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
	private final GlobalSingletonCache globalCache;

	public DailyStatisticWriter() {
		this.globalCache = GlobalSingletonCache.getInstance();
	}

	@Override
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		for (VideoStatistic item : chunk) {

			globalCache.addDailyData(
				VideoStatistic.builder()
					.videoId(item.getVideoId())
					.dailyViewCount(item.getDailyViewCount())
					.dailyDuration(item.getDailyDuration())
					.dailyAdViewCount(item.getDailyAdViewCount())
					.date(item.getDate())
					.build()
			); // 글로벌 캐시에 데이터 추가
		}
	}
}
