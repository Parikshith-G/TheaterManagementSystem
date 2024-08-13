package com.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.otp.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, String>{

}
