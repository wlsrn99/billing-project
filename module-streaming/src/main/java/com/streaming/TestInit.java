package com.streaming;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import com.streaming.entity.Video;
import com.streaming.entity.WatchHistory;
import com.streaming.repository.VideoRepository;
import com.streaming.repository.WatchedHistoryRepository;

@Component
@RequiredArgsConstructor
public class TestInit {

	private final VideoRepository videoRepository;
	private final WatchedHistoryRepository watchedHistoryRepository;

	@PostConstruct
	public void init() {
		// 예시 비디오 데이터 생성
		for (int i = 1; i <= 10; i++) {
			Video video = Video.builder()
				.title("Video " + i)
				.duration(120) // 예시로 120분 설정
				.viewCount(0L) // 초기 조회수 0으로 설정
				.adCount(0L) // 초기 광고 횟수 0으로 설정
				.createdAt(LocalDateTime.of(2024, 6, 1, 0, 0))
				.build();
			videoRepository.save(video);
		}


		// 모든 비디오를 가져오기
		List<Video> videos = videoRepository.findAll();

		// 랜덤 객체 생성
		Random random = new Random();

		// 예시 시청 기록 데이터 생성
		for (int i = 0; i < 200; i++) {
			// 랜덤 비디오 선택
			Video randomVideo = videos.get(random.nextInt(videos.size()));

			// 2024년 6월 1일부터 6월 15일까지 랜덤 날짜 설정
			LocalDate randomDate = LocalDate.of(2024, 6, 1).plusDays(random.nextInt(15));
			LocalDateTime randomDateTime = randomDate.atStartOfDay().plusHours(random.nextInt(24)).plusMinutes(random.nextInt(60));

			WatchHistory history = WatchHistory.builder()
				.videoId(randomVideo.getId()) // Video 객체 설정
				.userId((long) (random.nextInt(10) + 1)) // 랜덤 사용자 설정
				.lastWatchedPosition(random.nextInt(120)) // 예시로 랜덤 위치 설정
				.createdAt(randomDate)
				.watchedAt(randomDateTime)
				.duration(random.nextInt(121)) // 0부터 120 사이의 랜덤 재생 시간 설정
				.adViewCount(random.nextInt(6)) // 0부터 5 사이의 랜덤 광고 본 횟수 설정
				.build();

			watchedHistoryRepository.save(history);
		}
	}
}
