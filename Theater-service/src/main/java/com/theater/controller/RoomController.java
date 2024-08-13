package com.theater.controller;

import com.theater.dtos.RoomDto;
import com.theater.dtos.ShowDto;
import com.theater.entities.Room;
import com.theater.entities.Seat;
import com.theater.service.contract.IRoomsService;
import com.theater.service.contract.IShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")

public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private IRoomsService roomService;

    @Autowired
    private IShowService showService;

    @Autowired
    public RoomController(IRoomsService roomService, IShowService showService) {
        this.roomService = roomService;
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        logger.info("Getting all rooms");
        List<RoomDto> rooms = roomService.getAllRooms();
        logger.info("Retrieved {} rooms", rooms.size());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable(name = "id") Long id) {
        logger.info("Getting room by ID: {}", id);
        RoomDto room = roomService.getRoomById(id);
        logger.info("Retrieved room: {}", room);
        return ResponseEntity.ok(room);
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDTO) {
        logger.info("Creating room: {}", roomDTO);
        RoomDto createdRoom = roomService.createRoom(roomDTO);
        logger.info("Created room: {}", createdRoom);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDTO) {
        logger.info("Updating room with ID: {}", id);
        RoomDto updatedRoom = roomService.updateRoom(id, roomDTO);
        logger.info("Updated room: {}", updatedRoom);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        logger.info("Deleting room with ID: {}", id);
        roomService.deleteRoom(id);
        logger.info("Deleted room with ID: {}", id);
        return new ResponseEntity<String>("Done",HttpStatus.OK);
    }

    @PostMapping("/movie/{movieId}/show/{showId}")
    public ResponseEntity<ShowDto> addShowToRoom(
            @PathVariable(name = "showId") Long showId,
            @PathVariable(name = "movieId") Long movieId
    ) {
        logger.info("Adding show with ID: {} to movie with ID: {}", showId, movieId);
        ShowDto showDto = showService.addRoomToShow(showId, movieId);
        logger.info("Added show: {}", showDto);
        return new ResponseEntity<>(showDto, HttpStatus.CREATED);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Integer> getAvailableSeatOfRoom(@PathVariable(name = "roomId") Long roomId) {
        logger.info("Getting available seats for room ID: {}", roomId);
        Integer availableSeats = roomService.getAvailableSeatsofRoom(roomId);
        logger.info("Available seats for room ID {}: {}", roomId, availableSeats);
        return new ResponseEntity<>(availableSeats, HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<Room> exposedConverterDtoToObj(@RequestBody RoomDto dto) {
        logger.info("Converting RoomDto to Room: {}", dto);
        Room room = roomService.exposedConverterDtoToObj(dto);
        logger.info("Converted Room: {}", room);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/room/{roomId}/{seatNumber}")
    public Boolean updateSeatBooked(
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "seatNumber") Long seatNumber
    ) {
        logger.info("Updating seat booked status for room ID: {} and seat number: {}", roomId, seatNumber);
        Boolean result = roomService.updateSeatBooked(roomId, seatNumber);
        logger.info("Updated seat booked status for room ID: {} and seat number: {}: {}", roomId, seatNumber, result);
        return result;
    }

    @GetMapping("/seat/{seatId}")
    public Seat findSeatById(@PathVariable(name = "seatId") Long seatId) {
        logger.info("Finding seat by ID: {}", seatId);
        Seat seat = roomService.findSeatById(seatId);
//        logger.info("Found seat: {}", seat);
        return seat;
    }
}
