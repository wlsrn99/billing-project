package com.billing.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoBill;
import com.billing.repository.VideoBillRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyBillingWriter implements ItemWriter<VideoBill> {
	private final VideoBillRepository videoBillRepository;
	@Override
	public void write(Chunk<? extends VideoBill> chunk) throws Exception {
		videoBillRepository.saveAll(chunk.getItems());
	}
}
