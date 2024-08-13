package com.theater.booking.feign;


import com.theater.booking.config.FeignClientConfiguration;
import com.theater.booking.dto.RoomDto;
import com.theater.booking.entity.Seat;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "THEATER-SERVICE/api/v1/rooms",configuration = FeignClientConfiguration.class)
public interface FeignRoomCall {
	@GetMapping("/{id}")
	public ResponseEntity<RoomDto> getRoomById(@PathVariable(name = "id") Long id);

	@PutMapping("/{id}")
	public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDTO);

	@GetMapping("/room/{roomId}")
	public ResponseEntity<Integer> getAvailableSeatOfRoom(@PathVariable(name = "roomId") Long roomId);

	@PostMapping("/room/{roomId}/{seatNumber}")
	public Boolean updateSeatBooked(@PathVariable(name = "roomId") Long roomId,
			@PathVariable(name = "seatNumber") String seatNumber);

	@GetMapping("/seat/{seatId}")
	public Seat findSeatById(@PathVariable(name = "seatId") Long seatId);
}
