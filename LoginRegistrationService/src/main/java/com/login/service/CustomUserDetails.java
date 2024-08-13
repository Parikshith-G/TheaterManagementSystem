package com.login.service;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.login.model.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToString
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

	private static final long serialVersionUID = 1L;

	private String userName;
	private String password;
	private String role;

	public CustomUserDetails(User user) {

		this.userName = user.getEmail();
		this.password = user.getPassword();
		this.role = user.getRole();
		logger.debug("CustomUserDetails created for user: {}, role: {}", userName, role);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		logger.debug("Getting authorities for user: {}", userName);
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getPassword() {
		logger.debug("Getting password for user: {}", userName);
		return this.password;
	}

	@Override
	public String getUsername() {

		logger.debug("Getting username");
		return this.userName;
	}

	public String getRole() {
		logger.debug("Getting role for user: {}", userName);
		return this.role;
	}

	@Override
	public boolean isAccountNonExpired() {
		logger.debug("Checking if account is non-expired for user: {}", userName);
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		logger.debug("Checking if account is non-locked for user: {}", userName);
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		logger.debug("Checking if credentials are non-expired for user: {}", userName);
		return true;
	}

	@Override
	public boolean isEnabled() {
		logger.debug("Checking if account is enabled for user: {}", userName);
		return true;
	}

}
