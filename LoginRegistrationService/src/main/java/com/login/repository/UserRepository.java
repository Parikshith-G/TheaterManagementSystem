package com.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.login.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByName(String username);

	User findByNameOrEmail(String name,String email);

	Optional<User> findByEmail(String userName);


}
