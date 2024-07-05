package com.streaming.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streaming.dto.CreateRequestDTO;
import com.streaming.dto.CreateResponseDTO;
import com.streaming.dto.PauseResponseDTO;
import com.streaming.dto.SaveResponseDTO;
import com.streaming.service.StreamingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/streaming/api")
@RequiredArgsConstructor
public class StreamingContoller {
	private final StreamingService streamingService;

	@PostMapping("/create")
	public ResponseEntity<CreateResponseDTO> createVideo(@RequestHeader("userId") Long userId,@RequestBody CreateRequestDTO createRequestDTO){
		CreateResponseDTO responseDTO = streamingService.createVideo(userId, createRequestDTO);
		return ResponseEntity.ok(responseDTO);
	}


	@PostMapping("/{videoId}/play")
	public ResponseEntity<SaveResponseDTO> playVideo(@RequestHeader("userId") Long userId, @PathVariable Long videoId) {
		SaveResponseDTO responseDTO = streamingService.playVideo(userId, videoId);
		return ResponseEntity.ok(responseDTO);
	}

	@PostMapping("/{videoId}/pause")
	public ResponseEntity<PauseResponseDTO> pauseVideo(@RequestHeader("userId") Long userId, @PathVariable Long videoId) {
		PauseResponseDTO responseDTO = streamingService.pauseVideo(userId, videoId);
		return ResponseEntity.ok(responseDTO);
	}

}
