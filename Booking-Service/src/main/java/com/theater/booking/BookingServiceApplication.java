package com.theater.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class BookingServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(BookingServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting BookingServiceApplication...");
		SpringApplication.run(BookingServiceApplication.class, args);
		logger.info("BookingServiceApplication started successfully.");
	}

}
