package com.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(EurekaServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Eureka Service Application...");
		SpringApplication.run(EurekaServiceApplication.class, args);
		logger.info("Eureka Service Application started successfully.");
	}

}
