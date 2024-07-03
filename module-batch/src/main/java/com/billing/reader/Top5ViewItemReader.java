package com.billing.reader;

import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.billing.entity.DailyVideo;
import com.billing.repository.DailyVideoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@StepScope
@Slf4j
public class Top5ViewItemReader implements ItemReader<DailyVideo> {
	private final DailyVideoRepository dailyVideoRepository;
	private List<DailyVideo> dailyVideos;
	private int nextVideoIndex;

	@PostConstruct
	public void init() {
		dailyVideos = dailyVideoRepository.findTop5ByDateOrderByViewCountDesc(LocalDate.now().minusDays(1));
		nextVideoIndex = 0;
	}

	@Override
	public DailyVideo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		DailyVideo nextVideo = null;

		log.info("Top 5 Videos read start");

		if (nextVideoIndex < dailyVideos.size()) {
			nextVideo = dailyVideos.get(nextVideoIndex);
			nextVideoIndex++;
		}

		return nextVideo;
	}
}

