package com.streaming.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.dto.SaveResponseDTO;
import com.streaming.dto.VideoRequest;
import com.streaming.entity.WatcheHistory;
import com.streaming.service.StreamingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/streaming/api")
@RequiredArgsConstructor
public class StreamingContoller {
	private final StreamingService streamingService;

	@GetMapping("/test")
	public String test(){
		return "success";
	}

	@GetMapping("/{videoId}/play")
	public ResponseEntity<SaveResponseDTO> playVideo(@RequestHeader("userId") Long userId, @PathVariable Long videoId) {
		SaveResponseDTO video = streamingService.playVideo(userId, videoId);
		return ResponseEntity.ok(video);
	}

	@PostMapping("/{videoId}/pause")
	public ResponseEntity<WatcheHistory> pauseVideo(@RequestHeader("userId") Long userId, @PathVariable Long videoId) {
		WatcheHistory watcheHistory = streamingService.pauseVideo(userId, videoId);
		return ResponseEntity.ok(watcheHistory);
	}

}
