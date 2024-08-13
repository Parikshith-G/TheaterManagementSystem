package com.theater.controller;

import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;
import com.theater.service.contract.IShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")

public class ShowController {

    private static final Logger logger = LoggerFactory.getLogger(ShowController.class);

    private final IShowService showService;

    @Autowired
    public ShowController(IShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<ShowDto>> getAllShows() {
        logger.info("Fetching all shows");
        List<ShowDto> shows = showService.getAllShows();
        logger.info("Fetched {} shows", shows.size());
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id) {
        logger.info("Fetching show with ID: {}", id);
        ShowDto show = showService.getShowById(id);
        logger.info("Fetched show: {}", show);
        return ResponseEntity.ok(show);
    }

    @PostMapping
    public ResponseEntity<ShowDto> createShow(@RequestBody ShowDto showDTO) {
        logger.info("Creating show: {}", showDTO);
        ShowDto createdShow = showService.createShow(showDTO);
        logger.info("Created show: {}", createdShow);
        return new ResponseEntity<>(createdShow, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShowDto> updateShow(@PathVariable Long id, @RequestBody ShowDto showDTO) {
        logger.info("Updating show with ID: {}", id);
        ShowDto updatedShow = showService.updateShow(id, showDTO);
        logger.info("Updated show: {}", updatedShow);
        return ResponseEntity.ok(updatedShow);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        logger.info("Deleting show with ID: {}", id);
        showService.deleteShow(id);
        logger.info("Deleted show with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{showId}/movie/{movieId}")
    public ResponseEntity<MovieDto> addMovieToShow(
            @PathVariable(name = "movieId") Long movieId,
            @PathVariable(name = "showId") Long showId
    ) {
        logger.info("Adding movie with ID: {} to show with ID: {}", movieId, showId);
        MovieDto movieDto = showService.addMovieToShow(showId, movieId);
        logger.info("Added movie: {}", movieDto);
        return new ResponseEntity<>(movieDto, HttpStatus.CREATED);
    }

}
