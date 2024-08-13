package com.theater.booking.config;

import feign.Logger;
import feign.RequestInterceptor;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignClientConfiguration.class);

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	RequestInterceptor requestInterceptor() {

		return template -> {
			logger.info("Request to URL: {}", template.url());
			logger.info("Request Method: {}", template.method());
			logger.info("Request Headers: {}", template.headers());
			logger.info("Request Body: {}", template.requestBody());

		};
	}
}
