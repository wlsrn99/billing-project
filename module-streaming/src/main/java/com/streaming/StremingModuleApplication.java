package com.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StremingModuleApplication {
	public static void main(String[] args) {
		SpringApplication.run(StremingModuleApplication.class, args);
	}
}
