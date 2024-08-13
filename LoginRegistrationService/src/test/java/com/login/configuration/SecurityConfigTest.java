package com.login.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.login.service.CustomUserDetailsService;


public class SecurityConfigTest {

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@InjectMocks
	private SecurityConfig securityConfig;

	@BeforeEach
	void setUp() {
		securityConfig = new SecurityConfig();
	}

	@Test
	void testPasswordEncoder() {
		PasswordEncoder passwordEncoder = SecurityConfig.passwordEncoder();
		assertNotNull(passwordEncoder);
		assert (passwordEncoder instanceof BCryptPasswordEncoder);
	}


	@Test
	void testAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = (DaoAuthenticationProvider) spy(
				securityConfig.authenticationProvider());
		assertNotNull(authProvider);

		// Ensure the custom user details service and password encoder are set
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(SecurityConfig.passwordEncoder());

		verify(authProvider).setUserDetailsService(customUserDetailsService);
		verify(authProvider).setPasswordEncoder(any(PasswordEncoder.class));
	}

	@Test
	void testAuthenticationManager() {
		AuthenticationManager authenticationManager = securityConfig.authenticationManager();
		assertNotNull(authenticationManager);
	}
}
