package com.theater.controller;

import com.theater.dtos.RoomDto;
import com.theater.dtos.TheaterDto;
import com.theater.service.contract.ITheaterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theaters")

public class TheaterController {

    private static final Logger logger = LoggerFactory.getLogger(TheaterController.class);

    private final ITheaterService theaterService;

    @Autowired
    public TheaterController(ITheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public ResponseEntity<List<TheaterDto>> getAllTheaters() {
        logger.info("Fetching all theaters");
        List<TheaterDto> theaters = theaterService.getAllTheaters();
        logger.info("Fetched {} theaters", theaters.size());
        return ResponseEntity.ok(theaters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterDto> getTheaterById(@PathVariable(name = "id") Long id) {
        logger.info("Fetching theater with ID: {}", id);
        TheaterDto theater = theaterService.getTheaterById(id);
        logger.info("Fetched theater: {}", theater);
        return ResponseEntity.ok(theater);
    }

    @PostMapping
    public ResponseEntity<TheaterDto> createTheater(@RequestBody TheaterDto theaterDTO) {
        logger.info("Creating theater: {}", theaterDTO);
        TheaterDto createdTheater = theaterService.createTheater(theaterDTO);
        logger.info("Created theater: {}", createdTheater);
        return new ResponseEntity<>(createdTheater, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterDto> updateTheater(@PathVariable(name = "id") Long id, @RequestBody TheaterDto theaterDTO) {
        logger.info("Updating theater with ID: {}", id);
        TheaterDto updatedTheater = theaterService.updateTheater(id, theaterDTO);
        logger.info("Updated theater: {}", updatedTheater);
        return ResponseEntity.ok(updatedTheater);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable(name = "id") Long id) {
        logger.info("Deleting theater with ID: {}", id);
        theaterService.deleteTheater(id);
        logger.info("Deleted theater with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{theaterId}/rooms/{roomId}")
    public ResponseEntity<TheaterDto> addRoomToTheater(
            @PathVariable("theaterId") Long theaterId,
            @PathVariable("roomId") Long roomId
    ) {
        logger.info("Adding room with ID: {} to theater with ID: {}", roomId, theaterId);
        TheaterDto updatedTheater = theaterService.addRoomToTheater(theaterId, roomId);
        logger.info("Added room to theater: {}", updatedTheater);
        return ResponseEntity.ok(updatedTheater);
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsInTheater(@PathVariable(name = "id") Long id) {
        logger.info("Fetching rooms for theater with ID: {}", id);
        List<RoomDto> rooms = theaterService.getRoomsInTheater(id);
        logger.info("Fetched {} rooms for theater with ID: {}", rooms.size(), id);
        return ResponseEntity.ok(rooms);
    }
}
