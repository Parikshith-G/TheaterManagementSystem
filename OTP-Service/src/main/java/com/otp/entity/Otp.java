package com.otp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Otp")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Otp {

	@Id
	private String email;
	private String otp;

}
