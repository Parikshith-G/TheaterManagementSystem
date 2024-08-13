package com.login.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.login.model.User;
import com.login.repository.UserRepository;

@Service

public class CustomUserDetailsService implements UserDetailsService {
	
	private  Logger logger =  LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		logger.info("Loading user detials for username: {}",username);
		
		Optional<User> user = userRepository.findByEmail(username);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("Email Not Found "+username);
		}
		
		return new CustomUserDetails(user.get());
	}



}
