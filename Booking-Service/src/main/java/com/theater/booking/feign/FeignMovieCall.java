package com.theater.booking.feign;


import com.theater.booking.config.FeignClientConfiguration;
import com.theater.booking.dto.MovieDto;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "THEATER-SERVICE/api/v1/movies",configuration = FeignClientConfiguration.class)
public interface FeignMovieCall {

	@GetMapping("/{movieId}")
	MovieDto getMovieById(@PathVariable(name = "movieId") Long movieId);

	
}
