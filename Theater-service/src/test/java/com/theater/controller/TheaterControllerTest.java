package com.theater.controller;

import com.theater.dtos.RoomDto;
import com.theater.dtos.TheaterDto;
import com.theater.service.contract.ITheaterService;
import com.theater.exception.AppException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TheaterController.class)
public class TheaterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITheaterService theaterService;

    @Test
    public void getAllTheaters() throws Exception {
        List<TheaterDto> theaters = List.of(new TheaterDto(1L, "Theater1", "Description1",new ArrayList<>()));
        when(theaterService.getAllTheaters()).thenReturn(theaters);

        mockMvc.perform(get("/api/v1/theaters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(theaters.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Theater1"));
    }

    @Test
    public void getTheaterById() throws Exception {
        Long id = 1L;
        TheaterDto theater = new TheaterDto(id, "Theater1", "Description1",new ArrayList<>());
        when(theaterService.getTheaterById(id)).thenReturn(theater);

        mockMvc.perform(get("/api/v1/theaters/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Theater1"));
    }

    @Test
    public void getTheaterByIdNotFound() throws Exception {
        Long id = 1L;
        // Mock the service to throw the custom AppException
        when(theaterService.getTheaterById(id)).thenThrow(new AppException("Theater not found", HttpStatus.NOT_FOUND));

        // Perform the request and expect a 404 Not Found status
        mockMvc.perform(get("/api/v1/theaters/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Theater not found"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    public void createTheater() throws Exception {
        TheaterDto theaterDTO = new TheaterDto(1L, "Theater1", "Description1",new ArrayList<>());
        when(theaterService.createTheater(any(TheaterDto.class))).thenReturn(theaterDTO);

        mockMvc.perform(post("/api/v1/theaters")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Theater1\",\"description\":\"Description1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Theater1"));
    }

    @Test
    public void updateTheater() throws Exception {
        Long id = 1L;
        TheaterDto theaterDTO = new TheaterDto(id, "Updated Theater", "Updated Description",new ArrayList<>());
        when(theaterService.updateTheater(eq(id), any(TheaterDto.class))).thenReturn(theaterDTO);

        mockMvc.perform(put("/api/v1/theaters/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Updated Theater\",\"description\":\"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated Theater"));
    }

    @Test
    public void deleteTheater() throws Exception {
        Long id = 1L;
        doNothing().when(theaterService).deleteTheater(id);

        mockMvc.perform(delete("/api/v1/theaters/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void addRoomToTheater() throws Exception {
        Long theaterId = 1L;
        Long roomId = 2L;
        TheaterDto updatedTheater = new TheaterDto(theaterId, "Theater1", "Description1",new ArrayList<>());
        when(theaterService.addRoomToTheater(theaterId, roomId)).thenReturn(updatedTheater);

        mockMvc.perform(post("/api/v1/theaters/{theaterId}/rooms/{roomId}", theaterId, roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(theaterId))
                .andExpect(jsonPath("$.name").value("Theater1"));
    }

    @Test
    public void getRoomsInTheater() throws Exception {
        Long id = 1L;
        List<RoomDto> rooms = List.of(new RoomDto(1L,1L,"Room1",1,1,new ArrayList<>(),new ArrayList<>()));
        when(theaterService.getRoomsInTheater(id)).thenReturn(rooms);

        mockMvc.perform(get("/api/v1/theaters/{id}/rooms", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(rooms.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Room1"));
    }
}
