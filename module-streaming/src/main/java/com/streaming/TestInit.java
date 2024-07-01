package com.streaming;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.streaming.entity.AdDetail;
import com.streaming.entity.Video;
import com.streaming.entity.VideoAd;
import com.streaming.repository.AdDetailRepository;
import com.streaming.repository.VideoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestInit {
	private final VideoRepository videoRepository;
	private final AdDetailRepository adDetailRepository;

	@PostConstruct
	@Transactional
	public void init() {
		// 비디오 생성
		Video video1 = Video.builder()
			.title("Sample Video 1")
			.userId(10L)
			.duration(120)
			.viewCount(0)
			.build();

		Video video2 = Video.builder()
			.title("Sample Video 2")
			.userId(11L)
			.duration(90)
			.viewCount(0)
			.build();


		//광고 생성
		AdDetail ad1 = new AdDetail("광고1");
		AdDetail ad2 = new AdDetail("광고2");
		adDetailRepository.save(ad1);
		adDetailRepository.save(ad2);

		//비디오에 광고 삽입
		VideoAd videoAd1 = VideoAd.builder()
			.video(video1)
			.adDetail(ad1)
			.insertTime(20)
			.build();

		VideoAd videoAd2 = VideoAd.builder()
			.video(video1)
			.adDetail(ad2)
			.insertTime(30)
			.build();

		video1.getVideoAds().add(videoAd1);

		video1.getVideoAds().add(videoAd2);

		//광고 삽입 후 저장
		videoRepository.save(video1);
		videoRepository.save(video2);
	}

}
