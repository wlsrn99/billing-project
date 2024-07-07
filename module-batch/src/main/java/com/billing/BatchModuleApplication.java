package com.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BatchModuleApplication {
	public static void main(String[] args) {

		SpringApplication.run(BatchModuleApplication.class, args);
	}
}
