package com.theater.configuration;

import com.theater.dtos.TheaterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "theater-client",url=Constants.url+"theaters")
public interface TheaterClient {

    @GetMapping
    public ResponseEntity<List<TheaterDto>> getAllTheaters();

    @GetMapping("/{id}")
    public ResponseEntity<TheaterDto> getTheaterById(@PathVariable(name = "id") Long id);


    @PostMapping
    public ResponseEntity<TheaterDto> createTheater(@RequestBody TheaterDto theaterDTO);

    @PutMapping("/{id}")
    public ResponseEntity<TheaterDto> updateTheater(@PathVariable(name = "id") Long id, @RequestBody TheaterDto theaterDTO);

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable(name = "id") Long id);

    @PostMapping("/{theaterId}/rooms/{roomId}")
    public ResponseEntity<TheaterDto> addRoomToTheater(
            @PathVariable("theaterId") Long theaterId,
            @PathVariable("roomId") Long roomId
    );
}