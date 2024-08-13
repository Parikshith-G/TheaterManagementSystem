package com.theater.service.contract;

import com.theater.dtos.RoomDto;
import com.theater.entities.Room;
import com.theater.entities.Seat;

import java.util.List;

public interface IRoomsService {
	List<RoomDto> getAllRooms();

	RoomDto getRoomById(Long id);

	RoomDto createRoom(RoomDto roomDTO);

	RoomDto updateRoom(Long id, RoomDto roomDTO);

	void deleteRoom(Long id);

	Integer getAvailableSeatsofRoom(Long roomId);

	Room exposedConverterDtoToObj(RoomDto dto);

	Boolean updateSeatBooked(Long roomId, Long seatNumber);

	Seat findSeatById(Long seatId);
}
