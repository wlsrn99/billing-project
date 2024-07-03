package com.billing.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.billing.dto.DailyVideoDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Top5ViewItemWriter implements ItemWriter<DailyVideoDTO> {

	@Override
	public void write(Chunk<? extends DailyVideoDTO> chunk) throws Exception {
		log.info("Top 5 Videos write start");

		// 원본 리스트를 수정하지 않고 정렬하기 위해 새로운 리스트 생성
		List<DailyVideoDTO> sortedItems = new ArrayList<>(chunk.getItems());

		// 조회수 기준으로 내림차순 정렬
		sortedItems.sort((v1, v2) -> Long.compare(v2.getViewCount(), v1.getViewCount()));

		// 상위 5개 항목만 출력
		int limit = Math.min(5, sortedItems.size());
		for (int i = 0; i < limit; i++) {
			DailyVideoDTO item = sortedItems.get(i);
			System.out.println("Video: " + item.getTitle() + ", Views: " + item.getViewCount());
		}
	}
}
