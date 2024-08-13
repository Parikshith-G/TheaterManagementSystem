package com.theater.controller;

import com.theater.dtos.RoomDto;
import com.theater.dtos.ShowDto;
import com.theater.entities.Room;
import com.theater.entities.Seat;
import com.theater.service.contract.IRoomsService;
import com.theater.service.contract.IShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private IRoomsService roomService;

    @Mock
    private IShowService showService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    public void testGetAllRooms() throws Exception {
        RoomDto roomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, Collections.emptyList(), Collections.emptyList());
        when(roomService.getAllRooms()).thenReturn(List.of(roomDto));

        mockMvc.perform(get("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].theaterId").value(1))
                .andExpect(jsonPath("$[0].name").value("Room 1"))
                .andExpect(jsonPath("$[0].capacity").value(100))
                .andExpect(jsonPath("$[0].availableSeats").value(100));

        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    public void testGetRoomById() throws Exception {
        RoomDto roomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, Collections.emptyList(), Collections.emptyList());
        when(roomService.getRoomById(1L)).thenReturn(roomDto);

        mockMvc.perform(get("/api/v1/rooms/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.theaterId").value(1))
                .andExpect(jsonPath("$.name").value("Room 1"))
                .andExpect(jsonPath("$.capacity").value(100))
                .andExpect(jsonPath("$.availableSeats").value(100));

        verify(roomService, times(1)).getRoomById(1L);
    }

    @Test
    public void testCreateRoom() throws Exception {
        RoomDto roomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, Collections.emptyList(), Collections.emptyList());
        when(roomService.createRoom(any(RoomDto.class))).thenReturn(roomDto);

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"theaterId\":1,\"name\":\"Room 1\",\"capacity\":100,\"availableSeats\":100,\"showIds\":[],\"seatStatuses\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.theaterId").value(1))
                .andExpect(jsonPath("$.name").value("Room 1"))
                .andExpect(jsonPath("$.capacity").value(100))
                .andExpect(jsonPath("$.availableSeats").value(100));

        verify(roomService, times(1)).createRoom(any(RoomDto.class));
    }

    @Test
    public void testUpdateRoom() throws Exception {
        RoomDto roomDto = new RoomDto(1L, 1L, "Room 1", 100, 100, Collections.emptyList(), Collections.emptyList());
        when(roomService.updateRoom(eq(1L), any(RoomDto.class))).thenReturn(roomDto);

        mockMvc.perform(put("/api/v1/rooms/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"theaterId\":1,\"name\":\"Room 1\",\"capacity\":100,\"availableSeats\":100,\"showIds\":[],\"seatStatuses\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.theaterId").value(1))
                .andExpect(jsonPath("$.name").value("Room 1"))
                .andExpect(jsonPath("$.capacity").value(100))
                .andExpect(jsonPath("$.availableSeats").value(100));

        verify(roomService, times(1)).updateRoom(eq(1L), any(RoomDto.class));
    }

    @Test
    public void testDeleteRoom() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Done"));

        verify(roomService, times(1)).deleteRoom(1L);
    }

    @Test
    public void testAddShowToRoom() throws Exception {
        ShowDto showDto = new ShowDto(1L, 1L,1L,LocalDateTime.now(),LocalDateTime.now(),9.8D);
        when(showService.addRoomToShow(eq(1L), eq(1L))).thenReturn(showDto);

        mockMvc.perform(post("/api/v1/rooms/movie/{movieId}/show/{showId}", 1L, 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.movieId").value(1L));
        verify(showService, times(1)).addRoomToShow(eq(1L), eq(1L));
    }

    @Test
    public void testGetAvailableSeatOfRoom() throws Exception {
        when(roomService.getAvailableSeatsofRoom(1L)).thenReturn(50);

        mockMvc.perform(get("/api/v1/rooms/room/{roomId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(50));

        verify(roomService, times(1)).getAvailableSeatsofRoom(1L);
    }

    @Test
    public void testExposedConverterDtoToObj() throws Exception {
        Room room = new Room();
        when(roomService.exposedConverterDtoToObj(any(RoomDto.class))).thenReturn(room);

        mockMvc.perform(post("/api/v1/rooms/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"theaterId\":1,\"name\":\"Room 1\",\"capacity\":100,\"availableSeats\":100,\"showIds\":[],\"seatStatuses\":[]}"))
                .andExpect(status().isOk());

        verify(roomService, times(1)).exposedConverterDtoToObj(any(RoomDto.class));
    }

    @Test
    public void testUpdateSeatBooked() throws Exception {
        when(roomService.updateSeatBooked(1L, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/v1/rooms/room/{roomId}/{seatNumber}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(roomService, times(1)).updateSeatBooked(1L, 1L);
    }

    @Test
    public void testFindSeatById() throws Exception {
        Seat seat = new Seat();
        when(roomService.findSeatById(1L)).thenReturn(seat);

        mockMvc.perform(get("/api/v1/rooms/seat/{seatId}", 1L))
                .andExpect(status().isOk());

        verify(roomService, times(1)).findSeatById(1L);
    }
}
