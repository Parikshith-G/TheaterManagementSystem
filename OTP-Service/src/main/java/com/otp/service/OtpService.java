package com.otp.service;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.otp.entity.Otp;
import com.otp.repository.OtpRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OtpService {

	private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

	@Autowired
	private OtpRepository repository;

	private final Random random = new SecureRandom();

	@Autowired
	private JavaMailSender mailSender;

	public String generateOTP(String email) {
		String otp = String.format("%04d", random.nextInt(10000));
		Otp otpObj = new Otp(email, otp);

		sendOtpEmail(email, otp);
		repository.save(otpObj);
		logger.info("Generated OTP {} for email {}", otp, email);
		return otp;
	}

	public boolean validateOTP(String email, String otpCode) {
		Otp otp=repository.findById(email).orElseThrow(()->new RuntimeException("Email not found"));
		boolean isValid = otpCode.equals(otp.getOtp());
		logger.info("Validating OTP {} for email {}. Result: {}", otp, email, isValid);
		return isValid;
	}

	void sendOtpEmail(String to, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Your OTP Code");
		message.setText("Your OTP code is " + otp);
		mailSender.send(message);
		logger.info("Sent OTP email to {}", to);
	}

	public void deleteOtp(String email) {
		
		Otp otp=repository.findById(email).orElseThrow(()->new RuntimeException("Email not found"));
		repository.delete(otp);
		
	}

}
