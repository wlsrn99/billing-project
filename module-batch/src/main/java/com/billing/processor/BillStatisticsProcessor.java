package com.billing.processor;

import org.springframework.batch.item.ItemProcessor;

import com.billing.entity.VideoStatistic;
import com.billing.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BillStatisticsProcessor implements ItemProcessor<VideoStatistic, VideoStatistic> {
	private final VideoRepository videoRepository; //여기서 토탈 뷰 얻어서 단가 기준 계산

	@Override
	public VideoStatistic process(VideoStatistic item) throws Exception {
		// 영상의 총 조회수 가져오기
		int totalViews = videoRepository.findViewCountById(item.getVideo().getId());
		// 영상별 단가 계산
		double viewRate = getViewRate(totalViews);
		// 광고별 단가 계산
		double adRate = getAdRate(totalViews);

		//조회수 단가 계산
		long dailyViewCost = (long)Math.floor(calculateCost(item.getDailyViewCount(), viewRate));
		long weeklyViewCost = (long)Math.floor(calculateCost(item.getWeeklyViewCount(), viewRate));
		long monthlyViewCost = (long)Math.floor(calculateCost(item.getMonthlyViewCount(), viewRate));

		//광고수 단가 계산
		long dailyAdViewCost = (long)Math.floor(calculateCost(item.getDailyAdViewCount(), adRate));
		long weeklyAdViewCost = (long)Math.floor(calculateCost(item.getWeeklyAdViewCount(), adRate));
		long monthlyAdViewCost = (long)Math.floor(calculateCost(item.getMonthlyAdViewCount(), adRate));


		return VideoStatistic.builder()
			.date(item.getDate())
			.video(item.getVideo())
			.dailyViewCount(item.getDailyViewCount())
			.weeklyViewCount(item.getWeeklyViewCount())
			.monthlyViewCount(item.getMonthlyViewCount())
			.dailyAdViewCount(item.getDailyAdViewCount())
			.weeklyAdViewCount(item.getWeeklyAdViewCount())
			.monthlyAdViewCount(item.getMonthlyAdViewCount())
			.dailyDuration(item.getDailyDuration())
			.weeklyDuration(item.getWeeklyDuration())
			.monthlyDuration(item.getMonthlyDuration())
			.dailyViewBill(dailyViewCost)
			.weeklyViewBill(weeklyViewCost)
			.monthlyViewBill(monthlyViewCost)
			.dailyAdBill(dailyAdViewCost)
			.weeklyAdBill(weeklyAdViewCost)
			.monthlyAdBill(monthlyAdViewCost)
			.totalBill(monthlyViewCost + monthlyAdViewCost)
			.build();
	}

	private double getViewRate(int views) {
		if (views < 100000) {
			return 1.0;
		} else if (views < 500000) {
			return 1.1;
		} else if (views < 1000000) {
			return 1.3;
		} else {
			return 1.5;
		}
	}

	private double getAdRate(int views) {
		if (views < 100000) {
			return 10.0;
		} else if (views < 500000) {
			return 12.0;
		} else if (views < 1000000) {
			return 15.0;
		} else {
			return 20.0;
		}
	}

	private double calculateCost(long views, double rate) {
		return views * rate;
	}
}
