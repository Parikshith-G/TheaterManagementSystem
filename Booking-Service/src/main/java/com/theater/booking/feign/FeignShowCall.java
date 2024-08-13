package com.theater.booking.feign;



import com.theater.booking.config.FeignClientConfiguration;
import com.theater.booking.dto.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "THEATER-SERVICE/api/v1/shows",configuration = FeignClientConfiguration.class)
public interface FeignShowCall {

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id);


}
