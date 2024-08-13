package com.otp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.otp.entity.Wrapper;
import com.otp.service.OtpService;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

	private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

	@Autowired
	private OtpService otpService;

	@PostMapping("/generate")
	public ResponseEntity<String> generateOtp(@RequestBody String email) {
		logger.info("Received request to generate OTP for email: {}", email);
		otpService.generateOTP(email);
		logger.info("Generated OTP for email: {}", email);
		return ResponseEntity.ok("OTP sent to " + email);
	}

	@PostMapping("/validate")
	public ResponseEntity<Boolean> validateOtp(@RequestBody Wrapper wrapper) {
		logger.info("Received request to validate OTP for email: {}", wrapper.email());
		boolean isValid = otpService.validateOTP(wrapper.email(), wrapper.otp());
		logger.info("OTP validation result for email {}: {}", wrapper.email(), isValid);
		return ResponseEntity.ok(isValid);
	}
	
	@DeleteMapping("/delete/{email}")
	public void deleteOtp(@PathVariable(name = "email") String email) {
		logger.info("Received request to delete OTP for email: {}", email);
		otpService.deleteOtp(email);
		
	}
	

}
