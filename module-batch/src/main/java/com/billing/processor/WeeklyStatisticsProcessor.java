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
public class WeeklyStatisticsProcessor implements ItemProcessor<VideoStatistic, VideoStatistic> {
	private final DailyVideoRepository dailyVideoRepository;

	@Override
	public VideoStatistic process(VideoStatistic item) throws Exception {
		log.info("WeeklyStatisticsProcessor start");

		LocalDate startOfWeek = item.getDate().with(java.time.DayOfWeek.MONDAY);
		LocalDate endOfWeek = item.getDate().with(java.time.DayOfWeek.SUNDAY);

		List<DailyVideo> weeklyVideos = dailyVideoRepository.findAllByDateBetweenAndVideoId(startOfWeek, endOfWeek, item.getVideo().getId());

		int weeklyViewCount = weeklyVideos.stream().mapToInt(DailyVideo::getViewCount).sum();
		return VideoStatistic.builder()
			.video(item.getVideo())
			.date(item.getDate())
			.dailyViewCount(item.getDailyViewCount())
			.weeklyViewCount(weeklyViewCount)
			.monthlyViewCount(0) // 월간 프로세서에서 설정
			.build();
	}
}
