package com.email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmailWithPdfAttachment(String toEmail, String fromEmail, String subject, String body,
			byte[] pdfBytes, String pdfName) throws MessagingException, IOException {
		logger.info("Preparing to send email to: {}", toEmail);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("parikshithkugve@gmail.com");
		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(body);

		// Add the PDF attachment using the byte array
		helper.addAttachment(pdfName, new ByteArrayDataSource(pdfBytes, "application/pdf"));
		try {
			mailSender.send(message);
			logger.info("Email sent successfully to: {}", toEmail);
		} catch (Exception e) {
			logger.error("Failed to send email to: {}", toEmail, e);
			throw e;
		}

	}
}
