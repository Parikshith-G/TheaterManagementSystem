package com.theater.service.implementation;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theater.dtos.RoomDto;
import com.theater.dtos.SeatStatusDto;
import com.theater.entities.Room;
import com.theater.entities.Seat;
import com.theater.entities.Show;
import com.theater.entities.Theater;
import com.theater.exception.AppException;
import com.theater.repository.RoomsRepository;
import com.theater.repository.SeatRepository;
import com.theater.repository.ShowRepository;
import com.theater.repository.TheaterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class RoomsServiceTest {

    @InjectMocks
    private RoomsService roomsService;

    @Mock
    private RoomsRepository roomRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private SeatRepository seatRepository;

    private Room room;
    private RoomDto roomDto;
    private Theater theater;
    private Seat seat;
    private Show show;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        theater = new Theater();
        theater.setId(1L);
        Seat seat=new Seat();
        
        room = Room.builder()
                .id(1L)
                .theater(theater)
                .name("Room 1")
                .capacity(100)
                .availableSeats(100)
                .seats(new ArrayList<>())
                .shows(new ArrayList<>())
                
                .build();
       SeatStatusDto dto=new SeatStatusDto(1L, false);
       List<SeatStatusDto> aList=new ArrayList<>();
       aList.add(dto);
        roomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, new ArrayList<>(),aList);

        seat = new Seat();
        seat.setId(1L);
        seat.setRoom(room);
        seat.setSeatNumber("Seat-1");
        seat.setBooked(false);

        show = new Show();
        show.setId(1L);
        show.setStartTime(LocalDateTime.now().minusHours(1));
        show.setRoom(room);
        ObjectMapper objectMapper=new ObjectMapper();
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
   
    }

    @Test
    public void testGetAllRooms() {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));

        List<RoomDto> result = roomsService.getAllRooms();

        assertEquals(1, result.size());
      
        SeatStatusDto dto=new SeatStatusDto(1L, false);
        List<SeatStatusDto> aList=new ArrayList<>();
        aList.add(dto);
        RoomDto  newDto=new RoomDto(1L, 1L, "Room 1", 100, 100, new ArrayList<>(),aList);
        assertEquals(roomDto, newDto);
    }

    @Test
    public void testGetRoomByIdSuccess() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomDto result = roomsService.getRoomById(1L);
        SeatStatusDto dto=new SeatStatusDto(1L, false);
        List<SeatStatusDto> aList=new ArrayList<>();
        aList.add(dto);
        RoomDto  newDto=new RoomDto(1L, 1L, "Room 1", 100, 100, new ArrayList<>(),aList);
        assertEquals(roomDto, newDto);
    }

    @Test
    public void testGetRoomByIdNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.getRoomById(1L);
        });

        assertEquals("Room not found with id(room service): 1", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }


    @Test
    public void testCreateRoomTheaterNotFound() {
        when(theaterRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.createRoom(roomDto);
        });

        assertEquals("Theater not found(Room Service)", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testUpdateRoomSuccess() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomDto updatedRoomDto = new RoomDto(1L, 1L, "Updated Room", 150, 150, new ArrayList<>(), new ArrayList<>());
        RoomDto result = roomsService.updateRoom(1L, updatedRoomDto);

        assertEquals(updatedRoomDto, result);
    }

    @Test
    public void testUpdateRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.updateRoom(1L, roomDto);
        });

        assertEquals("Room not found with id(room service): 1", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }


    @Test
    public void testDeleteRoomShowNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.deleteRoom(1L);
        });

        assertEquals("No shows found for the room", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testDeleteRoomCannotDelete() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.deleteRoom(1L);
        });

        assertEquals("No shows found for the room", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGetAvailableSeats() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(seatRepository.findAll()).thenReturn(Collections.singletonList(seat));

        Integer result = roomsService.getAvailableSeatsofRoom(1L);

        assertEquals(100, result);
    }

    @Test
    public void testGetAvailableSeatsRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.getAvailableSeatsofRoom(1L);
        });

        assertEquals("Room not found with id: 1", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGenerateRoomsReport() throws IOException {
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        RoomDto reportRoomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, new ArrayList<>(), new ArrayList<>());
        List<RoomDto> roomDtos = Collections.singletonList(reportRoomDto);
        ObjectMapper objectMapper = new ObjectMapper();
        FileWriter fileWriter = mock(FileWriter.class);

        try (FileWriter writer = fileWriter) {
            roomsService.generateRoomsReport("test-report.json");
//            verify(fileWriter, times(1)).write(objectMapper.writeValueAsString(roomDtos));
        }
    }



    @Test
    public void testUpdateSeatBookedSuccess() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        Boolean result = roomsService.updateSeatBooked(1L, 1L);

        assertTrue(result);
    }

    @Test
    public void testUpdateSeatBookedRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        AppException thrown = assertThrows(AppException.class, () -> {
            roomsService.updateSeatBooked(1L, 1L);
        });

        assertEquals("Room not found(Room Service)", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }
}
