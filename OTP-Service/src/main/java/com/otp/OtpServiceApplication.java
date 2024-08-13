package com.otp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class OtpServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(OtpServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OtpServiceApplication.class, args);
		logger.info("OTP Service Application has started.");
	}

}
