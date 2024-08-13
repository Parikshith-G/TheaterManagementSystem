package com.theater.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import com.theater.booking.config.FeignClientConfiguration;
import com.theater.booking.dto.TheaterDto;


@FeignClient(name = "THEATER-SERVICE/api/v1/theaters",configuration = FeignClientConfiguration.class)
public interface FeignTheaterCall {
	@GetMapping("/{id}")
	public ResponseEntity<TheaterDto> getTheaterById(@PathVariable(name = "id") Long id);
}
