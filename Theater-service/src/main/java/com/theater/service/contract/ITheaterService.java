package com.theater.service.contract;

import com.theater.dtos.RoomDto;
import com.theater.dtos.TheaterDto;

import java.util.List;

public interface ITheaterService {


    List<TheaterDto> getAllTheaters();

    TheaterDto getTheaterById(Long id);

    TheaterDto createTheater(TheaterDto theaterDTO);

    TheaterDto updateTheater(Long id, TheaterDto theaterDTO);

    void deleteTheater(Long id);

    TheaterDto addRoomToTheater(Long theaterId,Long roomId);
    
    List<RoomDto> getRoomsInTheater(Long theaterId);
}
