package com.theater.configuration;

import com.theater.dtos.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "movie-service", url =  Constants.url+"movies")
public interface MovieClient {

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable(name = "id") Long id);

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDTO);

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable(name = "id") Long id, @RequestBody MovieDto movieDTO);


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable(name = "id") Long id);
}
