package com.billing.reader;

import java.util.Iterator;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoStatistic;
import com.billing.util.GlobalSingletonCache;

@Component
@StepScope
public class DailyBillingReader implements ItemReader<VideoStatistic> {
	private final Iterator<VideoStatistic> iterator;

	public DailyBillingReader() {
		GlobalSingletonCache globalCache = GlobalSingletonCache.getInstance();
		this.iterator = globalCache.getCacheData().iterator(); // 데이터 리스트의 iterator 가져오기
	}

	@Override
	public VideoStatistic read() throws Exception {
		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			return null; // 더 이상 읽을 데이터가 없을 경우 null 반환
		}
	}
}
