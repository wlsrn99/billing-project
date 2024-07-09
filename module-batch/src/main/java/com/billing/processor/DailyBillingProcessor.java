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
	private final Map<Long, Long> viewCountCache = new ConcurrentHashMap<>();
	private final Map<Long, Long> adCountCache = new ConcurrentHashMap<>();

	@Async //별도의 스레드에서 비동기적으로 실행되어야 초기화되는 중간에도 process스레드를 실행시킬 수 있음
	@PostConstruct
	public void init() {
		int pageSize = 10000; //10000개씩 로드
		long totalCount = videoRepository.count(); //데이터베이스에 있는 video의 전체 숫자
		for (int page = 0; page < (totalCount + pageSize - 1) / pageSize; page++) {
			Pageable pageable = PageRequest.of(page, pageSize);
			List<VideoBillingDTO> counts = videoRepository.findAllViewAndAdCountsPaged(pageable);
			for (VideoBillingDTO count : counts) {
				viewCountCache.put(count.getVideoId(), count.getViewCount());
				adCountCache.put(count.getVideoId(), count.getAdCount());
			}
			log.info("Loaded {} records into cache", (page + 1) * pageSize);
		}
		log.info("Cache initialization completed");
	}

	@Override
	public VideoBill process(VideoStatistic item) throws Exception {
		try {
			long totalView = getViewCount(item.getVideoId());
			long totalAdView = getAdCount(item.getVideoId());

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

	//캐시 누락 대비 메소드
	private long getViewCount(Long videoId) {
		return viewCountCache.computeIfAbsent(videoId, videoRepository::findViewCountById);
	}

	//캐시 누락 대비 메소드
	private long getAdCount(Long videoId) {
		return adCountCache.computeIfAbsent(videoId, videoRepository::findAdCountById);
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