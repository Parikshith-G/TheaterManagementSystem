package com.login.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.login.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Bean
	static PasswordEncoder passwordEncoder() {
		logger.debug("Creating PasswordEncoder bean");
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		logger.debug("Configuring SecurityFilterChain");
		http.csrf(c -> c.disable()).authorizeHttpRequests(request -> request.requestMatchers("/api/**").permitAll()
				.anyRequest().authenticated());
		logger.info("SecurityFilterChain configured successfully");

		return http.build();

	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		logger.debug("Creating DaoAuthenticationProvider bean");

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		authenticationProvider.setUserDetailsService(customUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		logger.info("DaoAuthenticationProvider configured successfully");

		return authenticationProvider;
	}

	@Bean
	AuthenticationManager authenticationManager() {
		logger.debug("Creating AuthenticationManager bean");
		AuthenticationManager manager = new ProviderManager(authenticationProvider());
		logger.info("AuthenticationManager configured successfully");
		return manager;
	}
}
