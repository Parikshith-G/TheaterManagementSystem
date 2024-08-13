package com.login.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.login.dto.AuthDto;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;
import com.login.exception.UserNotFoundException;
import com.login.model.User;
import com.login.repository.UserRepository;
import com.login.security.Jwtutil;

public class UserServiceImplTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@Mock
	private Jwtutil jwtutil;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRegisterUser_UserNotPresent() throws UserAlreadyPresentException {
		// Given
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userRepository.findAll()).thenReturn(users);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		// When
		Boolean result = userService.registerUser(user);

		// Then
		assertEquals(true, result);
		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testRegisterUser_UserAlreadyPresent() {
		// Given
		List<User> users = new ArrayList<>();
		User existingUser = new User();
		existingUser.setEmail("test@example.com");
		users.add(existingUser);
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userRepository.findAll()).thenReturn(users);

		// When & Then
		assertThrows(UserAlreadyPresentException.class, () -> {
			userService.registerUser(user);
		});
	}

	@Test
	public void testLogin_Success() throws InvalidCredentialException {
		// Given
		AuthDto loginUser = new AuthDto();
		loginUser.setEmail("test@example.com");
		loginUser.setPassword("password");

		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setRole("USER");
	    user.setName("Test User");
	    user.setId(1L);
	    user.setPhone("1234567890");
		Authentication authentication = mock(Authentication.class);
	    when(authentication.isAuthenticated()).thenReturn(true);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(jwtutil.generateToken(anyString(), anyString(), anyLong(), anyString(), anyString())).thenReturn("token");

		// When
		String token = userService.login(loginUser);

		// Then
		assertEquals("token", token);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtutil).generateToken(anyString(), anyString(), anyLong(), anyString(), anyString());
	}

	@Test
	public void testLogin_InvalidCredentials() {
		// Given
		AuthDto loginUser = new AuthDto();
		loginUser.setEmail("test@example.com");
		loginUser.setPassword("password");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Bad credentials"));

		// When & Then
		assertThrows(InvalidCredentialException.class, () -> {
			userService.login(loginUser);
		});
	}

	@Test
	public void testUpdateUser_UserFound() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");

		User updatedUser = new User();
		updatedUser.setId(1L);
		updatedUser.setEmail("test@example.com");
		updatedUser.setPassword("encodedPassword");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		// When
		User result = userService.updateUser(user);

		// Then
		assertEquals(updatedUser, result);
		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testUpdateUser_UserNotFound() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");

		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.updateUser(user);
		});
	}

	@Test
	public void testUpdateUserByEmail_UserFound() {
		// Given
		AuthDto dto = new AuthDto();
		dto.setEmail("test@example.com");
		dto.setPassword("newPassword");

		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("oldPassword");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		// When
		userService.updateUser(dto);

		// Then
		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testUpdateUserByEmail_UserNotFound() {
		// Given
		AuthDto dto = new AuthDto();
		dto.setEmail("test@example.com");
		dto.setPassword("newPassword");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.updateUser(dto);
		});
	}

	@Test
	public void testDeleteUser_UserFound() {
		// Given
		User user = new User();
		user.setId(1L);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		// When
		boolean result = userService.deleteUser(1L);

		// Then
		assertEquals(true, result);
		verify(userRepository).delete(any(User.class));
	}

	@Test
	public void testDeleteUser_UserNotFound() {
		// Given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.deleteUser(1L);
		});
	}

	@Test
	public void testGetAllExceptAdmin() {
		// Given
		List<User> users = new ArrayList<>();
		User user1 = new User();
		user1.setRole("USER");
		User user2 = new User();
		user2.setRole("ADMIN");
		users.add(user1);
		users.add(user2);

		when(userRepository.findAll()).thenReturn(users);

		// When
		List<User> result = userService.getAllExceptAdmin();

		// Then
		assertEquals(1, result.size());
		assertEquals("USER", result.get(0).getRole());
	}

	@Test
	public void testGetAllAdmins() {
		// Given
		List<User> users = new ArrayList<>();
		User user1 = new User();
		user1.setRole("USER");
		User user2 = new User();
		user2.setRole("ADMIN");
		users.add(user1);
		users.add(user2);

		when(userRepository.findAll()).thenReturn(users);

		// When
		List<User> result = userService.getAllAdmins();

		// Then
		assertEquals(1, result.size());
		assertEquals("ADMIN", result.get(0).getRole());
	}

	@Test
	public void testUpdateUserById_UserFound() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setRole("USER");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		// When
		boolean result = userService.updateUserById(1L);

		// Then
		assertEquals(true, result);
		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testUpdateUserById_UserNotFound() {
		// Given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When
		boolean result = userService.updateUserById(1L);

		// Then
		assertEquals(false, result);
	}

	@Test
	public void testUpdateUserByIdF_UserFound() {
		// Given
		User user = new User();
		user.setId(1L);
		user.setRole("ADMIN");

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		// When
		boolean result = userService.updateUserByIdF(1L);

		// Then
		assertEquals(true, result);
		verify(userRepository).save(any(User.class));
	}

	@Test
	public void testUpdateUserByIdF_UserNotFound() {
		// Given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When
		boolean result = userService.updateUserByIdF(1L);

		// Then
		assertEquals(false, result);
	}

	@Test
	public void testEmailDubs_EmailExists() {
		// Given
		User user = new User();
		user.setEmail("test@example.com");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		// When
		boolean result = userService.emailDubs("test@example.com");

		// Then
		assertEquals(true, result);
	}

	@Test
	public void testEmailDubs_EmailNotExists() {
		// Given
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// When
		boolean result = userService.emailDubs("test@example.com");

		// Then
		assertEquals(false, result);
	}

	@Test
	public void testStoreUUID_UserFound() throws UserNotFoundException {
		// Given
		User user = new User();
		user.setEmail("test@example.com");

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

		// When
		String uuid = userService.storeUUID("test@example.com");

		// Then
		assertEquals(36, uuid.length());
		verify(userRepository).findByEmail("test@example.com");
	}

	@Test
	public void testStoreUUID_UserNotFound() {
		// Given
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UserNotFoundException.class, () -> {
			userService.storeUUID("test@example.com");
		});
	}

	@Test
	public void testDoesEmailExist_EmailExists() {
		// Given
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

		// When
		boolean result = userService.doesEmailExist("test@example.com");

		// Then
		assertEquals(true, result);
	}

	@Test
	public void testDoesEmailExist_EmailNotExists() {
		// Given
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// When
		boolean result = userService.doesEmailExist("test@example.com");

		// Then
		assertEquals(false, result);
	}
}
