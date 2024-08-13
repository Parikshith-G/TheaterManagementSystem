package com.login.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.login.dto.AuthDto;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;
import com.login.exception.UserNotFoundException;
import com.login.model.User;
import com.login.repository.UserRepository;
import com.login.security.Jwtutil;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Autowired
	Jwtutil jwtutil;

	@Autowired
	private UserRepository userRepository;

	private String INVALID_PASSWORD_OR_EMAIL = "Invalid Email or Password";

	private String INVALID_PASSWORD_OR_EMAIL_LOGGING = "Invalid login attempt for user: {}";

	private final Map<String, String> uuidStorage = new ConcurrentHashMap<>();

	@Override
	public Boolean registerUser(User user) throws UserAlreadyPresentException {
		List<User> userList = userRepository.findAll();

		if (userList.isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			logger.info("User registered successfully: {}", user.getEmail());
			return true;
		} else {
			String email = user.getEmail();
			boolean flag = false;
			for (User u : userList) {
				if (u.getEmail().equalsIgnoreCase(email)) {
					flag = true;
				}
			}
			if (flag) {
				logger.info("User already exists: {}", email);
				throw new UserAlreadyPresentException("Can't add user. Already exits.");
			} else {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				userRepository.save(user);
				logger.info("User registered successfully: {}", user.getEmail());
				return true;
			}
		}
	}

	@Override
	public String login(AuthDto loginUser) throws InvalidCredentialException {

		String str = null;
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
			if (authentication.isAuthenticated()) {
				Optional<User> user = userRepository.findByEmail(loginUser.getEmail());
				if (user.isEmpty()) {
					logger.warn(INVALID_PASSWORD_OR_EMAIL_LOGGING, loginUser.getEmail());
					throw new InvalidCredentialException(INVALID_PASSWORD_OR_EMAIL);
				}
				User u = user.get();
				str = jwtutil.generateToken(u.getName(), u.getRole(), u.getId(), u.getEmail(), u.getPhone());
				return str;
			} else {
				logger.warn(INVALID_PASSWORD_OR_EMAIL_LOGGING, loginUser.getEmail());
				throw new InvalidCredentialException(INVALID_PASSWORD_OR_EMAIL);
			}
		} catch (BadCredentialsException e) {
			logger.warn(INVALID_PASSWORD_OR_EMAIL_LOGGING, loginUser.getEmail());
			throw new InvalidCredentialException(INVALID_PASSWORD_OR_EMAIL);
		}
	}

	@Override
	public User updateUser(User user) {

		User userOptional = userRepository.findById(user.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User  not found"));
		userOptional.setName(user.getName());
		userOptional.setEmail(user.getEmail());
		userOptional.setPassword(passwordEncoder.encode(user.getPassword()));
		userOptional.setPhone(user.getPhone());

		User updatedUser = userRepository.save(userOptional);
		logger.info("User updated successfully: {}", user.getId());
		return updatedUser;
	}

	@Override
	public void updateUser(AuthDto dto) {

		User userOptional = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User  not found"));

		userOptional.setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepository.save(userOptional);
		logger.info("User updated successfully: {}", userOptional.getId());

	}

	@Override
	public boolean deleteUser(Long id) {
		logger.info("Deleting user with ID: {}", id);
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User  not found"));
		userRepository.delete(user);
		logger.info("User deleted successfully: {}", id);
		return true;
	}

	@Override
	public List<User> getAllExceptAdmin() {
		logger.info("Fetching all users except admins");
		List<User> users = userRepository.findAll();
		return users.stream().filter(u -> u.getRole().equals("USER")).toList();

	}

	@Override
	public List<User> getAllAdmins() {
		logger.info("Fetching all admin users");
		List<User> users = userRepository.findAll();
		return users.stream().filter(u -> u.getRole().equals("ADMIN")).toList();
	}

	@Override
	public boolean updateUserById(Long id) {
		logger.info("Promoting user to admin with ID: {}", id);
		Optional<User> user = userRepository.findById(id);

		if (user.isPresent()) {

			User userS = user.get();
			userS.setRole("ADMIN");
			userRepository.save(userS);

			logger.info("User promoted to admin successfully: {}", id);
			return true;
		}

		logger.warn("User not found for promotion: {}", id);
		return false;
	}

	@Override
	public boolean updateUserByIdF(Long id) {
		logger.info("Demoting user to user role with ID: {}", id);
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			User userS = user.get();
			userS.setRole("USER");
			userRepository.save(userS);
			logger.info("User demoted to user role successfully: {}", id);
			return true;
		}

		logger.warn("User not found for demotion: {}", id);
		return false;
	}

	@Override
	public boolean emailDubs(String email) {

		logger.info("Checking if email exists: {}", email);
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			logger.info("Email exists: {}", email);
			return true;
		}
		return false;
	}

	@Override
	public String storeUUID(String email) throws UserNotFoundException {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User with given email not found"));
		String uuid = UUID.randomUUID().toString();
		uuidStorage.put(email, uuid);
		return uuid;

	}

	@Override
	public boolean doesEmailExist(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

}
