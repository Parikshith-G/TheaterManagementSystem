package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
	private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

	public static void main(String[] args) {
		logger.info("Starting ApiGatewayApplication...");
		SpringApplication.run(ApiGatewayApplication.class, args);
		logger.info("ApiGatewayApplication started successfully.");
	}

}
