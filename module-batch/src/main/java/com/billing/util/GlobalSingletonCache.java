package com.billing.util;

import com.billing.entity.VideoStatistic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class GlobalSingletonCache {
	private static GlobalSingletonCache globalSingletonCache;
	private final Map<Long, VideoStatistic> cacheData;
	private long totalAdViewCount; // AdViewCount의 누적값을 저장하는 변수

	private GlobalSingletonCache() {
		cacheData = new HashMap<>();
		totalAdViewCount = 0;
	}

	public static GlobalSingletonCache getInstance() {
		if (globalSingletonCache == null) {
			globalSingletonCache = new GlobalSingletonCache();
		}
		return globalSingletonCache;
	}

	// VideoStatistic 객체를 추가하는 메소드
	public void addDailyData(VideoStatistic data) {
		long videoId = data.getVideoId();
		if (cacheData.containsKey(videoId)) { // 존재 여부를 확인
			VideoStatistic existingData = cacheData.get(videoId);
			// 필요한 경우 통계 업데이트
			existingData.updateDailyDuration(existingData.getDailyDuration() + data.getDailyDuration());
			existingData.updateDailyViewCount(existingData.getDailyViewCount() + data.getDailyViewCount());
			existingData.updateDailyAdViewCount(existingData.getDailyAdViewCount() + data.getDailyAdViewCount());
		} else {
			cacheData.put(videoId, data);
		}
		// 전체 누적값 업데이트
		totalAdViewCount += data.getDailyAdViewCount();
	}

	// 전체 캐시 데이터를 반환하는 메소드
	public List<VideoStatistic> getCacheData() {
		return new ArrayList<>(cacheData.values()); // 모든 캐시 데이터를 List로 반환
	}

	public void clearCache() {
		cacheData.clear();
		totalAdViewCount = 0; // 초기화
	}
}
