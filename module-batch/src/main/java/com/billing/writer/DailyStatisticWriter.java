package com.billing.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoStatisticRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED) //기본적인 트랜잭션 격리 수준
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
	private final VideoStatisticRepository videoStatisticRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW) //각 write 호출 마다 새로운 트랜잭션 시작
	@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000)) // 실패시 최대 3번까지 재시작
	public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
		try {
			videoStatisticRepository.saveAll(chunk.getItems());
			log.info("Successfully saved {} VideoStatistic items", chunk.size());
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation while saving VideoStatistic items", e);
			throw e; // 예외를 다시 던져 재시도 로직 활성화
		} catch (Exception e) {
			log.error("Error occurred while saving VideoStatistic items", e);
			throw e; // 예외를 다시 던져 재시도 로직 활성화
		}
	}
}