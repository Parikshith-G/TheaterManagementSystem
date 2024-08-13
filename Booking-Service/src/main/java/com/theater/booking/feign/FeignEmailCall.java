package com.theater.booking.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.theater.booking.config.FeignClientConfiguration;
import com.theater.booking.dto.EmailWrapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name="EMAIL-SERVICE/api/v1",configuration = FeignClientConfiguration.class)
public interface FeignEmailCall {


	@PostMapping("/pdf")
	public ResponseEntity<byte[]> generatePdf(@RequestBody EmailWrapper wrapper);

}


///,url = Constants.urlMail