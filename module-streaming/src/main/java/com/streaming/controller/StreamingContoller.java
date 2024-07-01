package com.streaming.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.streaming.dto.VideoRequest;
import com.streaming.entity.Video;
import com.streaming.entity.WatcheHistory;
import com.streaming.service.StreamingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/streaming/api")
@RequiredArgsConstructor
public class StreamingContoller {
	private final StreamingService streamingService;

	@GetMapping("/{videoId}/play")
	public ResponseEntity<Video> playVideo(@RequestBody VideoRequest request, @PathVariable Long videoId) {
		Video video = streamingService.playVideo(request.getUserId(), videoId);
		return ResponseEntity.ok(video);
	}

	@PostMapping("/{videoId}/pause")
	public ResponseEntity<WatcheHistory> pauseVideo(@RequestBody VideoRequest request, @PathVariable Long videoId) {
		WatcheHistory watcheHistory = streamingService.pauseVideo(request.getUserId(), videoId);
		return ResponseEntity.ok(watcheHistory);
	}

}
