package com.billing.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoBill;
import com.billing.repository.VideoBillRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBillingWriter implements ItemWriter<VideoBill> {
	private final VideoBillRepository videoBillRepository;

	@Override
	// 예외 발생 시 최대 3번 재시도, 재시도 간 1초 대기
	@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
	public void write(Chunk<? extends VideoBill> chunk) throws Exception {
		try {
			// 청크의 모든 아이템을 한 번에 저장
			videoBillRepository.saveAll(chunk.getItems());
			log.info("Successfully saved {} VideoBill items", chunk.size());
		} catch (DataIntegrityViolationException e) {
			// 데이터 무결성 위반 예외 처리
			log.error("Data integrity violation while saving VideoBill items", e);
			throw e; // 예외를 다시 던져 재시도 로직 활성화
		} catch (Exception e) {
			// 기타 모든 예외 처리
			log.error("Error occurred while saving VideoBill items", e);
			throw e; // 예외를 다시 던져 재시도 로직 활성화
		}
	}
}
