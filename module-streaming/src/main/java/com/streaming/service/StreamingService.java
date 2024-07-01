package com.streaming.service;

import static com.streaming.exception.VideoException.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.dto.SaveResponseDTO;
import com.streaming.entity.Video;
import com.streaming.entity.VideoAd;
import com.streaming.entity.WatcheHistory;
import com.streaming.exception.VideoErrorCode;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.WatchedHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingService {
	private final VideoRepository videoRepository;
	private final WatchedHistoryRepository watchedHistoryRepository;

	@Transactional
	public SaveResponseDTO playVideo(Long userId, Long videoId){
		Video video = videoRepository.findById(videoId)
			.orElseThrow(() -> new VideoNotFoundException(VideoErrorCode.VIDEO_NOT_FOUND));

		// 조회수 증가
		video.increaseViewCount(video.getViewCount() + 1);

		videoRepository.save(video);

		// 시청기록 확인
		WatcheHistory watcheHistory = watchedHistoryRepository.findByUserIdAndVideoId(userId, videoId)
			.orElseGet(() ->{
				WatcheHistory newHistory = WatcheHistory.builder()
					.userId(userId)
					.videoId(videoId)
					.lastWatchedPosition(0)
					.build();

				return newHistory;
			});

		watcheHistory.clearLastPlayTime(LocalDateTime.now());
		watchedHistoryRepository.save(watcheHistory);

		return new SaveResponseDTO(video, watcheHistory.getLastWatchedPosition());
	}

	@Transactional
	public WatcheHistory pauseVideo(Long userId, Long videoId){
		WatcheHistory watcheHistory = watchedHistoryRepository.findByUserIdAndVideoId(userId, videoId)
			.orElseThrow(() -> new VideoNotFoundException(VideoErrorCode.VIDEO_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(watcheHistory.getLastPlayTime(), now);
		int secondsWatched = (int) duration.getSeconds();

		//현재 시점에서 재생 버튼을 누른 지점
		int startWatched = watcheHistory.getLastWatchedPosition();
		watcheHistory.updateWatchedPosition(secondsWatched);
		watcheHistory.clearLastPlayTime(null);
		//현재 시점에서 정지 버튼을 누른 지점
		int endWatched = watcheHistory.getLastWatchedPosition();
		Video video = videoRepository.findById(videoId).get();

		//정지 버튼을 누른 지점이 영상의 길이보다 크다면 원래 영상 길이로 초기화
		if(endWatched >= video.getDuration()){
			watcheHistory.resetWatchedPosition(video.getDuration());
			endWatched = watcheHistory.getLastWatchedPosition();
		}

		//영상 시청 길이에 따라 광고 시청 횟수 증가
		incrementAdViews(video, startWatched, endWatched);



		videoRepository.save(video);
		watchedHistoryRepository.save(watcheHistory);
		return watcheHistory;
	}

	/**
	 *
	 * @param video 현재 동영상
	 * @param startWatched 재생 버튼을 누른 위치
	 * @param endWatched 정지 버튼을 누른 위치
	 */
	private void incrementAdViews(Video video, int startWatched, int endWatched) {
		List<VideoAd> videoAds = video.getVideoAds();
		for (VideoAd videoAd : videoAds) {
			if(videoAd.isAdWatchedDuring(startWatched,endWatched)){
				videoAd.incrementViewCount();
			}
		}
	}
}
