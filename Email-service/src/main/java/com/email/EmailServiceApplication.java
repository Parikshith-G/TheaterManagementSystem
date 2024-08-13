package com.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableDiscoveryClient
public class EmailServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting EmailServiceApplication...");
		SpringApplication.run(EmailServiceApplication.class, args);
		logger.info("EmailServiceApplication started successfully.");
	}

}
