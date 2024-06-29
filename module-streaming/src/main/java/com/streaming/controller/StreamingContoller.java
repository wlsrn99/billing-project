package com.streaming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamingContoller {
	@GetMapping("/test")
	public String test(){
		return "success";
	}
}
