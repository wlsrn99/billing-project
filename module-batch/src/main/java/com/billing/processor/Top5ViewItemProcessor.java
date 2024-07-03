package com.billing.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.billing.dto.DailyVideoDTO;
import com.billing.entity.DailyVideo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Top5ViewItemProcessor implements ItemProcessor<DailyVideo, DailyVideoDTO> {

	@Override
	public DailyVideoDTO process(DailyVideo dailyVideo) throws Exception {
		log.info("Top 5 Videos process start");
		// DailyVideo 객체를 DailyVideoDTO로 변환
		return new DailyVideoDTO(dailyVideo.getVideo().getTitle(), dailyVideo.getViewCount());
	}
}
