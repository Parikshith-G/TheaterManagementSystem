package com.otp.controller;

import com.otp.entity.Wrapper;
import com.otp.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OtpControllerTest {

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(otpController).build();
    }

    @Test
    public void generateOtp() throws Exception {
        String email = "test@example.com";

        // Mock the service call
        when(otpService.generateOTP(anyString())).thenReturn("1234");


        // Perform the request and verify the response
        mockMvc.perform(post("/api/v1/otp/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + email + "\""))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("OTP sent to " + "\""+email+"\""));
    }

    @Test
    public void validateOtp() throws Exception {
        String email = "test@example.com";
        String otp = "123456";
        Wrapper wrapper = new Wrapper(email, otp);

        // Mock the service call
        when(otpService.validateOTP(email, otp)).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/v1/otp/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"otp\": \"" + otp + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));
    }

    @Test
    public void validateOtp_InvalidOtp() throws Exception {
        String email = "test@example.com";
        String otp = "wrongOtp";
        Wrapper wrapper = new Wrapper(email, otp);

        // Mock the service call
        when(otpService.validateOTP(email, otp)).thenReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/v1/otp/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"otp\": \"" + otp + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"));
    }
}
