package com.billing.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoStatisticRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyBillingWriter implements ItemWriter<VideoStatistic> {
	private final VideoStatisticRepository videoStatisticV1Repository;
	@Override
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		videoStatisticV1Repository.saveAll(chunk.getItems());
	}
}
