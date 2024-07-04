package com.streaming;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.entity.AdDetail;
import com.streaming.entity.DailyVideo;
import com.streaming.entity.Video;
import com.streaming.entity.VideoAd;
import com.streaming.repository.AdDetailRepository;
import com.streaming.repository.DailyVideoRepository;
import com.streaming.repository.VideoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestInit {
	private final VideoRepository videoRepository;
	private final AdDetailRepository adDetailRepository;
	private final DailyVideoRepository dailyVideoRepository;
	private final Random random = new Random();

	@PostConstruct
	@Transactional
	public void init() {
		// 비디오 생성
		List<Video> videos = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			Video video = Video.builder()
				.title("Sample Video " + i)
				.userId((long) (10 + i))
				.duration(60 + random.nextInt(61)) // 60초에서 120초 사이의 랜덤 길이
				.viewCount(0) // 초기 조회수를 0으로 설정
				.build();
			videos.add(video);
		}

		// 광고 생성
		AdDetail ad1 = new AdDetail("광고1");
		AdDetail ad2 = new AdDetail("광고2");
		adDetailRepository.save(ad1);
		adDetailRepository.save(ad2);

		// 비디오에 광고 삽입
		for (Video video : videos) {
			VideoAd videoAd1 = VideoAd.builder().video(video).adDetail(ad1).insertTime(random.nextInt(30)).build();
			VideoAd videoAd2 = VideoAd.builder().video(video).adDetail(ad2).insertTime(30 + random.nextInt(30)).build();
			video.getVideoAds().add(videoAd1);
			video.getVideoAds().add(videoAd2);
		}

		// 광고 삽입 후 저장
		videoRepository.saveAll(videos);

		// 데일리 비디오 데이터 생성 및 저장
		LocalDate startDate = LocalDate.of(2024, 7, 1);
		LocalDate endDate = LocalDate.of(2024, 7, 31);
		List<DailyVideo> dailyVideos = new ArrayList<>();

		for (Video video : videos) {
			LocalDate date = startDate;
			while (!date.isAfter(endDate)) {
				int dailyViewCount = random.nextInt(500); // 0에서 500 사이의 랜덤 조회수
				DailyVideo dailyVideo = DailyVideo.builder()
					.video(video)
					.date(date)
					.duration(random.nextInt(1, video.getDuration())) // 1에서 비디오 길이 사이의 랜덤 재생 시간
					.viewCount(dailyViewCount)
					.adViewCount(random.nextInt(100))
					.build();

				// 비디오의 전체 조회수 업데이트
				video.incrementViewCount(dailyViewCount);

				dailyVideos.add(dailyVideo);
				date = date.plusDays(1);
			}
		}

		dailyVideoRepository.saveAll(dailyVideos);
		// 비디오의 전체 조회수 업데이트 후 저장
		videoRepository.saveAll(videos);
	}
}
