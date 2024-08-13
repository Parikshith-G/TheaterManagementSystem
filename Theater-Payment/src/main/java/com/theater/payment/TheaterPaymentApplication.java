package com.theater.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class TheaterPaymentApplication {

	private static final Logger logger = LoggerFactory.getLogger(TheaterPaymentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TheaterPaymentApplication.class, args);
		logger.info("Theater Payment Application has started.");
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
				logger.info("CORS mappings configured: allow all origins and methods.");
			}
		};
	}
}
