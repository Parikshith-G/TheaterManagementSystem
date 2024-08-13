package com.login.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.login.model.User;
import com.login.repository.UserRepository;

public class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testLoadUserByUsername_UserFound() {
		// Given
		User mockUser = new User();
		mockUser.setEmail("test@example.com");
		mockUser.setPassword("password");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

		// When
		CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService
				.loadUserByUsername("test@example.com");

		// Then

		assertEquals("test@example.com", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
		verify(userRepository).findByEmail("test@example.com");
	}

	@Test
	public void testLoadUserByUsername_UserNotFound() {
		// Given
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UsernameNotFoundException.class, () -> {
			customUserDetailsService.loadUserByUsername("nonexistent@example.com");
		});
		verify(userRepository).findByEmail("nonexistent@example.com");
	}
}
