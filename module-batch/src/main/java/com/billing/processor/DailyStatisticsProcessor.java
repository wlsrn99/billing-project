package com.billing.processor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyStatisticsProcessor implements ItemProcessor<VideoStatistic, VideoStatistic> {

	@Override
	public VideoStatistic process(VideoStatistic item) throws Exception {
		return item;
	}
}
