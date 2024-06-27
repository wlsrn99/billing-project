package com.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryModuleApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiscoveryModuleApplication.class, args);
	}
}
