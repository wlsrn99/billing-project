package com.billing.writer;


import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoStatisticRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoStatisticWriter implements ItemWriter<VideoStatistic> {
	private final VideoStatisticRepository videoStatisticRepository;


	@Override
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		videoStatisticRepository.saveAll(chunk.getItems());
	}
}

