package com.billing.processor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.billing.entity.DailyVideo;
import com.billing.entity.VideoStatistic;
import com.billing.repository.DailyVideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MonthlyStatisticsProcessor implements ItemProcessor<VideoStatistic, VideoStatistic> {
	private final DailyVideoRepository dailyVideoRepository;

	@Override
	public VideoStatistic process(VideoStatistic item) throws Exception {
		log.info("MonthlyStatisticsProcessor process start");

		LocalDate startOfMonth = item.getDate().withDayOfMonth(1);
		LocalDate endOfMonth = item.getDate().withDayOfMonth(item.getDate().lengthOfMonth());

		List<DailyVideo> monthlyVideos = dailyVideoRepository.findAllByDateBetweenAndVideoId(startOfMonth, endOfMonth, item.getVideo().getId());
		int monthlyViewCount = monthlyVideos.stream().mapToInt(DailyVideo::getViewCount).sum();

		return VideoStatistic.builder()
			.weeklyViewCount(item.getWeeklyViewCount())
			.dailyViewCount(item.getDailyViewCount())
			.date(item.getDate())
			.video(item.getVideo())
			.monthlyViewCount(monthlyViewCount)
			.build();
	}
}
