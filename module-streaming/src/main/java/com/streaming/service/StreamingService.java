package com.streaming.service;

import static com.streaming.exception.VideoException.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.entity.Video;
import com.streaming.entity.WatcheHistory;
import com.streaming.exception.VideoErrorCode;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.WatchedHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamingService {
	private final VideoRepository videoRepository;
	private final WatchedHistoryRepository watchedHistoryRepository;

	@Transactional
	public Video playVideo(Long userId, Long videoId){
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
		return video;
	}

	@Transactional
	public WatcheHistory pauseVideo(Long userId, Long videoId){
		WatcheHistory watcheHistory = watchedHistoryRepository.findByUserIdAndVideoId(userId, videoId)
			.orElseThrow(() -> new VideoNotFoundException(VideoErrorCode.VIDEO_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(watcheHistory.getLastPlayTime(), now);
		int secondsWatched = (int) duration.getSeconds();

		watcheHistory.updateWatchedPosition(secondsWatched);
		watcheHistory.clearLastPlayTime(null);

		watchedHistoryRepository.save(watcheHistory);
		return watcheHistory;
	}
}
