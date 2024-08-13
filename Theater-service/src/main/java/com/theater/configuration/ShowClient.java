package com.theater.configuration;


import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "show-service",url=Constants.url+"shows")
public interface ShowClient {

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id);

    @PostMapping
    public ResponseEntity<ShowDto> createShow(@RequestBody ShowDto showDTO);

    @GetMapping
    public ResponseEntity<List<ShowDto>> getAllShows();

    @PutMapping("/{id}")
    public ResponseEntity<ShowDto> updateShow(@PathVariable Long id, @RequestBody ShowDto showDTO);


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id);

    @PostMapping("/{showId}/room/{roomId}")
    public ResponseEntity<MovieDto> addMovieToShow(
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "showId") Long showId
    );
}