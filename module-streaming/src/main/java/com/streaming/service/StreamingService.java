package com.streaming.service;

import static com.streaming.exception.VideoException.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.aop.annotations.WriteOnly;
import com.streaming.dto.CreateRequestDTO;
import com.streaming.dto.CreateResponseDTO;
import com.streaming.dto.PauseResponseDTO;
import com.streaming.dto.StartResponseDTO;
import com.streaming.entity.AdDetail;
import com.streaming.entity.Video;
import com.streaming.entity.VideoAd;
import com.streaming.entity.WatchHistory;
import com.streaming.exception.VideoErrorCode;
import com.streaming.repository.AdDetailRepository;
import com.streaming.repository.VideoAdRepository;
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
	private final VideoAdRepository videoAdRepository;
	private final AdDetailRepository adDetailRepository;

	@WriteOnly
	@Transactional
	public StartResponseDTO playVideo(Long userId, Long videoId) {
		Video video = videoRepository.findById(videoId)
			.orElseThrow(() -> new VideoNotFoundException(VideoErrorCode.VIDEO_NOT_FOUND));

		// 조회수 증가
		video.increaseViewCount(video.getViewCount() + 1);
		videoRepository.save(video);

		LocalDate now = LocalDate.now();
		WatchHistory watchHistory = watchedHistoryRepository.findByUserIdAndVideoIdAndCreatedAt(userId, videoId, now)
			.orElseGet(() -> {
				return WatchHistory.builder()
					.userId(userId)
					.videoId(videoId)
					.build();
			});

		LocalDateTime nowTime = LocalDateTime.now();
		watchHistory.updateWatchedAt(nowTime);
		watchedHistoryRepository.save(watchHistory);

		return new StartResponseDTO(video, watchHistory.getLastWatchedPosition());
	}

	@WriteOnly
	@Transactional
	public PauseResponseDTO pauseVideo(Long userId, Long videoId) {
		WatchHistory watchHistory = watchedHistoryRepository.findByRecentHistory(userId, videoId)
			.orElseThrow(() -> new VideoNotFoundException(VideoErrorCode.VIDEO_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(watchHistory.getWatchedAt(), now);
		int secondsWatched = (int)duration.getSeconds();

		//현재 시점에서 재생 버튼을 누른 지점
		int startWatched = watchHistory.getLastWatchedPosition();
		watchHistory.updateWatchedPosition(secondsWatched);

		//현재 시점에서 정지 버튼을 누른 지점
		int endWatched = watchHistory.getLastWatchedPosition();
		Video video = videoRepository.findById(videoId).get();

		//정지 버튼을 누른 지점이 영상의 길이보다 크다면 원래 영상 길이로 초기화
		if (endWatched >= video.getDuration()) {
			watchHistory.resetWatchedPosition(video.getDuration());
			endWatched = watchHistory.getLastWatchedPosition();
		}

		//오늘 날짜의 재생한 시간 업데이트
		watchHistory.updateDuration(endWatched - startWatched);

		//영상 시청 길이에 따라 광고 시청 횟수 증가
		int adViewCount = getAdViewCount(video, startWatched, endWatched);

		//오늘 날짜의 광고 본 횟수 업데이트
		watchHistory.updateAdViewCount(adViewCount);
		video.increaseAdCount(video.getAdCount() + adViewCount);


		watchedHistoryRepository.save(watchHistory);
		videoRepository.save(video);

		return PauseResponseDTO.builder()
			.videoId(watchHistory.getVideoId())
			.userId(watchHistory.getUserId())
			.lastWatchedPosition(watchHistory.getLastWatchedPosition())
			.duration(watchHistory.getDuration())
			.build();
	}

	@WriteOnly
	@Transactional
	public CreateResponseDTO createVideo(Long userId, CreateRequestDTO createRequestDTO) {
		// 새 비디오 생성
		Video newVideo = Video.builder()
			.userId(userId)
			.title(createRequestDTO.getTitle())
			.duration(createRequestDTO.getDuration())
			.viewCount(0)
			.build();

		int adCount = 0;
		List<VideoAd> videoAds = new ArrayList<>();
		if (newVideo.getDuration() >= 300) {
			adCount = newVideo.getDuration() / 300;

			// AdDetail을 priority 순으로 adCount갯수만큼 가져오기
			List<AdDetail> adDetails = adDetailRepository.findByOrderByPriorityAsc(PageRequest.of(0, adCount));
			for (int i = 1; i <= adCount; i++) {
				VideoAd videoAd = VideoAd.builder()
					.video(newVideo)
					.insertTime(300 * i)
					.adDetail(adDetails.get(i-1))
					.build();
				videoAds.add(videoAd);
			}
		}

		//새 비디오 저장
		videoRepository.save(newVideo);
		//비디오에 연결되어 있는 광고들 저장
		videoAdRepository.saveAll(videoAds);

		return CreateResponseDTO.builder()
			.title(newVideo.getTitle())
			.duration(newVideo.getDuration())
			.adCount(adCount)
			.build();
	}


	private int getAdViewCount(Video video, int startWatched, int endWatched) {
		List<VideoAd> videoAds = video.getVideoAds();
		int count = 0;
		for (VideoAd videoAd : videoAds) {
			if (videoAd.isAdWatchedDuring(startWatched, endWatched)) {
				videoAd.incrementViewCount();
				count++;
			}
		}
		return count;
	}
}
