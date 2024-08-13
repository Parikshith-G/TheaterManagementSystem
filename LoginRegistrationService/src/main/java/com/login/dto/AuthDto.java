package com.login.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
	@NotEmpty(message = "Please provide username and email...")
	private String email;
	@NotEmpty(message = "Please give a password...")
	private String password;
}
