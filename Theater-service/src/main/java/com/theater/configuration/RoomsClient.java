package com.theater.configuration;


import com.theater.dtos.RoomDto;
import com.theater.dtos.ShowDto;
import com.theater.entities.Room;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "room-service", url =  Constants.url+"rooms")
public interface RoomsClient {

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms();


    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable(name="id") Long id);

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDTO);


    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDTO);

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id);

    @PostMapping("/show/{showId}/movie/{movieId}")
    public ResponseEntity<ShowDto> addShowToRoom(
            @PathVariable(name = "showId") Long showId,
            @PathVariable(name = "movieId") Long movieId
    );

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Integer> getAvailableSeatOfRoom(@PathVariable(name = "roomId")Long roomId);

    @PostMapping("/room")
    public ResponseEntity<Room> exposedConverterDtoToObj(@RequestBody RoomDto dto);

    @PostMapping("/room/{roomId}/{seatNumber}")
    public Boolean updateSeatBooked(
            @PathVariable(name = "roomId") Long roomId,
            @PathVariable(name = "seatNumber") String seatNumber
    );
}
