package com.billing.reader;

import java.time.LocalDate;
import java.util.Iterator;
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
public class DailyVideoReader implements ItemReader<DailyVideo> {
	private final DailyVideoRepository dailyVideoRepository;
	private Iterator<DailyVideo> dailyVideoIterator;

	@PostConstruct
	public void init() {
		List<DailyVideo> dailyVideos = dailyVideoRepository.findByDateEquals(LocalDate.now());
		dailyVideoIterator = dailyVideos.iterator();
	}

	@Override
	public DailyVideo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		log.info("Reading daily videos from database");
		if (dailyVideoIterator.hasNext()) {
			return dailyVideoIterator.next();
		} else {
			return null;
		}
	}
}
