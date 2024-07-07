package com.billing.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.billing.entity.VideoBill;
import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyBillingProcessor implements ItemProcessor<VideoStatistic, VideoBill>{
	private final VideoRepository videoRepository;
	@Override
	public VideoBill process(VideoStatistic item) throws Exception {
		long totalView = videoRepository.findViewCountById(item.getVideoId());
		long totalAdView = videoRepository.findAdCountById(item.getVideoId());

		long dailyViewBill = calculateViewCost(totalView, item.getDailyViewCount());
		long dailyAdBill = calculateViewCost(totalAdView, item.getDailyAdViewCount());

		return VideoBill.builder()
			.videoId(item.getVideoId())
			.date(item.getDate())
			.dailyViewBill(dailyViewBill)
			.dailyAdBill(dailyAdBill)
			.totalBill(dailyViewBill + dailyAdBill)
			.build();
	}

	private long calculateViewCost(long totalViews, long views) {
		long currentTotalViews = totalViews - views;

		double[] rates = {1.0, 1.1, 1.3, 1.5};
		return calculateCost(currentTotalViews, views, rates);
	}

	public long calculateAdCost(long totalViews, long views) {
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
