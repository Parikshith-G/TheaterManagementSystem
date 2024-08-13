package com.email.controller;


import com.email.EmailService;
import com.email.PdfService;
import com.email.entities.EmailWrapper;
import jakarta.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PdfServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PdfService pdfService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGeneratePdf() throws IOException, MessagingException {
        // Create a sample EmailWrapper
        EmailWrapper wrapper = new EmailWrapper(
                "TheaterName",
                "Location",
                "RoomName",
                "theater@example.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "UserName",
                "MovieName",
                "SeatName",
                "BookingRef123",
                "user@example.com",
                100.0
        );

        // Mock the service response
        doNothing().when(emailService).sendEmailWithPdfAttachment(
                any(String.class), any(String.class), any(String.class), any(String.class), any(byte[].class), any(String.class));

        // Invoke the service method
        ResponseEntity<byte[]> response = pdfService.generatePdf(wrapper);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());

        // Verify that emailService.sendEmailWithPdfAttachment was called once
        verify(emailService, times(1)).sendEmailWithPdfAttachment(
                eq(wrapper.userMail()), eq(wrapper.theaterContact()), eq("Movie ticket"), eq("This is your movie ticket"), any(byte[].class), any(String.class));
    }
}