package com.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.login.dto.AuthDto;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;

import com.login.model.User;
import com.login.security.Jwtutil;
import com.login.service.UserService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/user")
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private Jwtutil jwtutil;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@PostMapping("/register")
	public ResponseEntity<Object> registerUser(@RequestBody @Valid User user) throws UserAlreadyPresentException {
		logger.info("Registering user with email: {}", user.getEmail());
		if (Boolean.TRUE.equals(userService.registerUser(user))) {
			String p = encoder.encode(user.getPassword());
			user.setPassword(p);
			logger.info("User registered successfully: {}", user.getEmail());
			return generateResponse("Added successfully", HttpStatus.OK, user, "");

		} else {
			logger.error("Failed to register user: {}", user.getEmail());
			return generateResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, user, "");
		}
	}

	@PutMapping("/reset")
	public ResponseEntity<String> resetPassword(@RequestBody @Valid AuthDto dto) throws InvalidCredentialException {

		userService.updateUser(dto);
		String token = userService.login(dto);
		return new ResponseEntity<>(token, HttpStatus.OK);

	}

	@PostMapping("/login")
	public ResponseEntity<Object> loginUser(@RequestBody @Valid AuthDto loginUser) throws InvalidCredentialException {
		logger.info("User login attempt: {}", loginUser.getEmail());
//		String p = encoder.encode(loginUser.getPassword());
		String token = userService.login(loginUser);

//		loginUser.setPassword(p);
		logger.info("User logged in successfully: {}", loginUser.getEmail());

		return generateResponse("Login successful", HttpStatus.OK, loginUser, token);
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<Boolean> emailDups(@PathVariable(name = "email") String email) {
		logger.info("Checking for email duplicates: {}", email);
		return new ResponseEntity<>(userService.emailDubs(email), HttpStatus.OK);
	}

	@GetMapping("/_email/{email}")
	public ResponseEntity<Boolean> emailExists(@PathVariable(name = "email") String email) {
		logger.info("Checking for email existance: {}", email);
		return new ResponseEntity<>(userService.doesEmailExist(email), HttpStatus.OK);
	}

	@PutMapping("/user/edit/{token}")
	public ResponseEntity<String> editUser(@RequestBody User user, @PathVariable(name = "token") String token)
			throws InvalidCredentialException {
		logger.info("Editing user with token: {}", token);
		jwtutil.validateToken(token);

		String pwd = user.getPassword();
		User userUpd = userService.updateUser(user);

		AuthDto dto = new AuthDto(userUpd.getEmail(), pwd);
		String tkn = userService.login(dto);

		logger.info("User edited successfully: {}", user.getEmail());
		return new ResponseEntity<>(tkn, HttpStatus.OK);
	}

	@DeleteMapping("/user/{id}/{token}")
	public ResponseEntity<Boolean> deleteUser(@PathVariable(name = "id") Long id,
			@PathVariable(name = "token") String token) {
		jwtutil.validateToken(token);
		logger.info("Deleting user with ID: {}", id);
		return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
	}

	@DeleteMapping("/admin/{id}/{token}")
	public ResponseEntity<Boolean> deleteUserOverrideAdmin(@PathVariable(name = "id") Long id,
			@PathVariable(name = "token") String token) {
		jwtutil.validateToken(token);
		logger.info("Admin override delete user with ID: {}", id);
		String role = jwtutil.getUserRoleFromToken(token);
		if (!"ADMIN".equals(role)) {
			logger.warn("Unauthorized admin delete attempt by user with role: {}", role);
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
		}

		logger.warn(" admin deleted user with role: {}", HttpStatus.OK);
		return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
	}

	@DeleteMapping("/god/{id}/{token}")
	public ResponseEntity<Boolean> deleteUserOverrideGod(@PathVariable(name = "id") Long id,
			@PathVariable(name = "token") String token) {
		jwtutil.validateToken(token);
		logger.info("God override delete user with ID: {}", id);
		String role = jwtutil.getUserRoleFromToken(token);

		if ("ADMIN".equals(role) || "USER".equals(role)) {
			logger.warn("Unauthorized god delete attempt by user with role: {}", role);
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
		}

		if (!"GOD".equals(role)) {
			logger.warn("Unauthorized god delete attempt by user with role: {}", role);
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

		}

		return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
	}

	@GetMapping("/admin/all/{token}")
	public ResponseEntity<List<User>> getAllUsersExceptAdmins(@PathVariable(name = "token") String token) {
		jwtutil.validateToken(token);
		logger.info("Fetching all users except admins");
		return new ResponseEntity<>(userService.getAllExceptAdmin(), HttpStatus.OK);
	}

	@GetMapping("/god/admins/{token}")
	public ResponseEntity<List<User>> getAllAdmins(@PathVariable(name = "token") String token) {
		jwtutil.validateToken(token);
		logger.info("Fetching all admins");
		return new ResponseEntity<>(userService.getAllAdmins(), HttpStatus.OK);
	}

	@PutMapping("/god/promote/{id}/{token}")
	public ResponseEntity<Boolean> promoteUser(@PathVariable(name = "token") String token,
			@PathVariable(name = "id") Long id) {
		jwtutil.validateToken(token);

		logger.info("Promoting user with ID: {}", id);
		String role = jwtutil.getUserRoleFromToken(token);
		if ("GOD".equals(role)) {

			return new ResponseEntity<>(userService.updateUserById(id), HttpStatus.OK);
		} else {

			logger.warn("Unauthorized promote attempt by user with role: {}", role);
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

		}

	}

	@PutMapping("/god/demote/{id}/{token}")
	public ResponseEntity<Boolean> demoteUser(@PathVariable(name = "token") String token,
			@PathVariable(name = "id") Long id) {
		jwtutil.validateToken(token);
		logger.info("Demoting user with ID: {}", id);
		String role = jwtutil.getUserRoleFromToken(token);
		if ("GOD".equals(role)) {
			return new ResponseEntity<>(userService.updateUserByIdF(id), HttpStatus.OK);
		} else {
			logger.warn("Unauthorized demote attempt by user with role: {}", role);
			return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);

		}

	}

	private ResponseEntity<Object> generateResponse(String message, HttpStatus httpStatus, Object responseObj,
			String token) {
		Map<String, Object> map = new HashMap<>();
		map.put("Message", message);
		map.put("Status", httpStatus.value());
		map.put("Data", responseObj);
		map.put("Token", token);
		logger.info("Generating response: {}", message);
		return new ResponseEntity<>(map, httpStatus);
	}

}
