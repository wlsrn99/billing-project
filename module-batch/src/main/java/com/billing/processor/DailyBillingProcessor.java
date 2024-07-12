package com.billing.processor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;

import com.billing.dto.VideoBillingDTO;
import com.billing.entity.VideoBill;
import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyBillingProcessor implements ItemProcessor<VideoStatistic, VideoBill> {
	private final VideoRepository videoRepository;
	// 카페인 캐시
	// 중복될게없다

	@Override
	public VideoBill process(VideoStatistic item) throws Exception {
		try {
			long totalView = videoRepository.findViewCountById(item.getVideoId());
			long totalAdView = videoRepository.findAdCountById(item.getVideoId());

			long dailyViewBill = calculateViewCost(totalView, item.getDailyViewCount());
			long dailyAdBill = calculateAdCost(totalAdView, item.getDailyAdViewCount());

			return VideoBill.builder()
				.videoId(item.getVideoId())
				.date(item.getDate())
				.dailyViewBill(dailyViewBill)
				.dailyAdBill(dailyAdBill)
				.totalBill(dailyViewBill + dailyAdBill)
				.build();
		} catch (Exception e) {
			log.error("Error processing item: " + item, e);
			throw new RuntimeException(e);
		}
	}


	private long calculateViewCost(long totalViews, long views) {
		long currentTotalViews = totalViews - views;
		double[] rates = {1.0, 1.1, 1.3, 1.5};
		return calculateCost(currentTotalViews, views, rates);
	}

	private long calculateAdCost(long totalViews, long views) {
		long currentTotalViews = totalViews - views;
		double[] rates = {10.0, 12.0, 15.0, 20.0};
		return calculateCost(currentTotalViews, views, rates);
	}

	private static long calculateCost(long currentTotalViews, long remainingViews, double[] rates) {
		double cost = 0.0;
		int[] thresholds = {100000, 500000, 1000000};

		for (int i = 0; i < thresholds.length; i++) {
			if (currentTotalViews < thresholds[i]) {
				long viewsInThisTier = Math.min(remainingViews, thresholds[i] - currentTotalViews);
				cost += (viewsInThisTier * rates[i]);
				remainingViews -= viewsInThisTier;
				currentTotalViews += viewsInThisTier;
			}

			if (remainingViews <= 0) {
				break;
			}
		}

		if (remainingViews > 0) {
			cost += (remainingViews * rates[rates.length - 1]);
		}

		return (long)cost;
	}
}