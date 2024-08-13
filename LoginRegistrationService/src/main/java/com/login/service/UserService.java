package com.login.service;

import java.util.List;
import com.login.dto.AuthDto;
import com.login.exception.InvalidCredentialException;
import com.login.exception.UserAlreadyPresentException;
import com.login.exception.UserNotFoundException;
import com.login.model.User;

public interface UserService {

	public Boolean registerUser(User user) throws UserAlreadyPresentException;

	public String login(AuthDto loginUser) throws InvalidCredentialException;

	List<User> getAllAdmins();

	public User updateUser(User user);

	public void updateUser(AuthDto dto);

	public boolean deleteUser(Long id);

	public List<User> getAllExceptAdmin();

	public boolean updateUserById(Long id);

	public boolean updateUserByIdF(Long id);

	public boolean emailDubs(String email);

	public String storeUUID(String email) throws UserNotFoundException;

	public boolean doesEmailExist(String email);
}
