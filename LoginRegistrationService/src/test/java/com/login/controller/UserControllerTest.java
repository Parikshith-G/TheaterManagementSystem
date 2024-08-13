package com.login.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.login.dto.AuthDto;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;
import com.login.model.User;
import com.login.security.Jwtutil;
import com.login.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	@Mock
	private Jwtutil jwtutil;

	@Mock
	private BCryptPasswordEncoder encoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void registerUser_success() throws UserAlreadyPresentException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userService.registerUser(user)).thenReturn(true);
		when(encoder.encode("password")).thenReturn("encodedPassword");

		ResponseEntity<Object> response = userController.registerUser(user);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		assertEquals("Added successfully", ((Map<String, Object>) response.getBody()).get("Message"));
	}

	@Test
	void registerUser_failure() throws UserAlreadyPresentException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userService.registerUser(user)).thenReturn(false);

		ResponseEntity<Object> response = userController.registerUser(user);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Something went wrong", ((Map<String, Object>) response.getBody()).get("Message"));
	}

	@Test
	void resetPassword_success() throws InvalidCredentialException {
		AuthDto authDto = new AuthDto("test@example.com", "newPassword");

		doNothing().when(userService).updateUser(authDto);

		when(userService.login(authDto)).thenReturn("token");

		ResponseEntity<String> response = userController.resetPassword(authDto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("token", response.getBody());
	}

	@Test
	void loginUser_success() throws InvalidCredentialException {
		AuthDto authDto = new AuthDto("test@example.com", "password");

		when(encoder.encode("password")).thenReturn("encodedPassword");
		when(userService.login(authDto)).thenReturn("token");

		ResponseEntity<Object> response = userController.loginUser(authDto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Login successful", ((Map<String, Object>) response.getBody()).get("Message"));
		assertEquals("token", ((Map<String, Object>) response.getBody()).get("Token"));
	}

	@Test
	void emailDups() {
		String email = "test@example.com";

		when(userService.emailDubs(email)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.emailDups(email);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void emailExists() {
		String email = "test@example.com";

		when(userService.doesEmailExist(email)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.emailExists(email);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void editUser_success() throws InvalidCredentialException {
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		String token = "validToken";
	     doNothing().when(jwtutil).validateToken(token);

		
		when(userService.updateUser(user)).thenReturn(user);
		when(userService.login(any(AuthDto.class))).thenReturn("newToken");

		ResponseEntity<String> response = userController.editUser(user, token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("newToken", response.getBody());
	}

	@Test
	void deleteUser_success() {
		Long id = 1L;
		String token = "validToken";
	     doNothing().when(jwtutil).validateToken(token);
		when(userService.deleteUser(id)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.deleteUser(id, token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void deleteUserOverrideAdmin_success() {
		Long id = 1L;
		String token = "validAdminToken";

		
		doNothing().when(jwtutil).validateToken(token);
		when(jwtutil.getUserRoleFromToken(token)).thenReturn("ADMIN");
		when(userService.deleteUser(id)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.deleteUserOverrideAdmin(id, token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void deleteUserOverrideGod_success() {
		Long id = 1L;
		String token = "validGodToken";

		doNothing().when(jwtutil).validateToken(token);
		when(jwtutil.getUserRoleFromToken(token)).thenReturn("GOD");
		when(userService.deleteUser(id)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.deleteUserOverrideGod(id, token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void getAllUsersExceptAdmins_success() {
		String token = "validToken";
		List<User> users = Arrays.asList(new User(), new User());

		doNothing().when(jwtutil).validateToken(token);
		when(userService.getAllExceptAdmin()).thenReturn(users);

		ResponseEntity<List<User>> response = userController.getAllUsersExceptAdmins(token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(users, response.getBody());
	}

	@Test
	void getAllAdmins_success() {
		String token = "validToken";
		List<User> admins = Arrays.asList(new User(), new User());

		doNothing().when(jwtutil).validateToken(token);
		when(userService.getAllAdmins()).thenReturn(admins);

		ResponseEntity<List<User>> response = userController.getAllAdmins(token);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(admins, response.getBody());
	}

	@Test
	void promoteUser_success() {
		Long id = 1L;
		String token = "validGodToken";

		doNothing().when(jwtutil).validateToken(token);
		when(jwtutil.getUserRoleFromToken(token)).thenReturn("GOD");
		when(userService.updateUserById(id)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.promoteUser(token, id);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}

	@Test
	void demoteUser_success() {
		Long id = 1L;
		String token = "validGodToken";

		doNothing().when(jwtutil).validateToken(token);
		when(jwtutil.getUserRoleFromToken(token)).thenReturn("GOD");
		when(userService.updateUserByIdF(id)).thenReturn(true);

		ResponseEntity<Boolean> response = userController.demoteUser(token, id);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody());
	}
}
