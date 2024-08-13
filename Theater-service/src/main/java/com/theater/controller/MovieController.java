package com.theater.controller;

import com.theater.dtos.MovieDto;
import com.theater.service.contract.IMovieService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private IMovieService movieService;
    

    @Autowired
    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
      
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        logger.info("Getting all movies");
        List<MovieDto> movies = movieService.getAllMovies();
        logger.info("Retrieved {} movies", movies.size());
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable(name = "id") Long id) {
        logger.info("Getting movie by ID: {}", id);
        MovieDto movie = movieService.getMovieById(id);
        logger.info("Retrieved movie: {}", movie);
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDTO) {
        logger.info("Creating movie: {}", movieDTO);
        MovieDto createdMovie = movieService.createMovie(movieDTO);
        logger.info("Created movie: {}", createdMovie);
        return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable(name = "id") Long id, @RequestBody MovieDto movieDTO) {
        logger.info("Updating movie with ID: {}", id);
        MovieDto updatedMovie = movieService.updateMovie(id, movieDTO);
        logger.info("Updated movie: {}", updatedMovie);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable(name = "id") Long id) {
        logger.info("Deleting movie with ID: {}", id);
        movieService.deleteMovie(id);
        logger.info("Deleted movie with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
