package com.otp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.otp.entity.Otp;
import com.otp.repository.OtpRepository;

import org.springframework.mail.SimpleMailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OtpServiceTest {
	@Mock
	private OtpRepository otpRepository;

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private OtpService otpService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGenerateOTP() {
		String email = "test@example.com";
		String generatedOtp = otpService.generateOTP(email);

		assertNotNull(generatedOtp);
		assertTrue(generatedOtp.matches("\\d{4}")); // Check if OTP is 4 digits

		verify(otpRepository, times(1)).save(any(Otp.class));
		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	@Test
	void testValidateOTP_Success() {
		String email = "test@example.com";
		String otpCode = "1234";
		Otp otp = new Otp(email, otpCode);

		when(otpRepository.findById(email)).thenReturn(java.util.Optional.of(otp));

		boolean isValid = otpService.validateOTP(email, otpCode);

		assertTrue(isValid);
		verify(otpRepository, times(1)).findById(email);
	}

	@Test
	void testValidateOTP_Failure() {
		String email = "test@example.com";
		String otpCode = "1234";

		when(otpRepository.findById(email)).thenReturn(java.util.Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> {
			otpService.validateOTP(email, otpCode);
		});

		assertEquals("Email not found", exception.getMessage());
		verify(otpRepository, times(1)).findById(email);
	}

	@Test
	void testSendOtpEmail() {
		String email = "test@example.com";
		String otp = "1234";

		otpService.sendOtpEmail(email, otp);

		verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	@Test
	void testDeleteOtp() {
		String email = "test@example.com";
		Otp otp = new Otp(email, "1234");

		when(otpRepository.findById(email)).thenReturn(java.util.Optional.of(otp));

		otpService.deleteOtp(email);

		verify(otpRepository, times(1)).findById(email);
		verify(otpRepository, times(1)).delete(otp);
	}

	@Test
	void testDeleteOtp_EmailNotFound() {
		String email = "test@example.com";

		when(otpRepository.findById(email)).thenReturn(java.util.Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> {
			otpService.deleteOtp(email);
		});

		assertEquals("Email not found", exception.getMessage());
		verify(otpRepository, times(1)).findById(email);
	}
}