package com.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableDiscoveryClient
@EnableJpaAuditing
@SpringBootApplication
public class LoginRegistrationServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(LoginRegistrationServiceApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Login Registration Service Application...");
		SpringApplication.run(LoginRegistrationServiceApplication.class, args);
		logger.info("Login Registration Service Application started successfully.");
	}

}
