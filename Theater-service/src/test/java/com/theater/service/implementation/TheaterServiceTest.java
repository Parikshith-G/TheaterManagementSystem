package com.theater.service.implementation;

import com.theater.dtos.RoomDto;
import com.theater.dtos.SeatStatusDto;
import com.theater.dtos.TheaterDto;
import com.theater.entities.Room;
import com.theater.entities.Show;
import com.theater.entities.Theater;
import com.theater.exception.AppException;
import com.theater.repository.RoomsRepository;
import com.theater.repository.ShowRepository;
import com.theater.repository.TheaterRepository;
import com.theater.service.contract.ITheaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TheaterServiceTest {

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private TheaterService theaterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTheaters() {
        // Given
        Theater theater1 = new Theater();
        theater1.setId(1L);
        theater1.setName("Theater 1");
        theater1.setLocation("Location 1");
        theater1.setRooms(new ArrayList<>());

        Theater theater2 = new Theater();
        theater2.setId(2L);
        theater2.setName("Theater 2");
        theater2.setLocation("Location 2");
        theater2.setRooms(new ArrayList<>());

        List<Theater> theaters = List.of(theater1, theater2);
        when(theaterRepository.findAll()).thenReturn(theaters);

        // When
        List<TheaterDto> result = theaterService.getAllTheaters();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(theaterRepository, times(1)).findAll();
    }

    @Test
    void getTheaterById() {
        // Given
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");
        theater.setRooms(new ArrayList<>());

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));

        // When
        TheaterDto result = theaterService.getTheaterById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(theaterRepository, times(1)).findById(1L);
    }

    @Test
    void getTheaterById_throwsException() {
        // Given
        when(theaterRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> theaterService.getTheaterById(1L));
        assertEquals("Theater not found (theater service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createTheater() {
        // Given
        TheaterDto theaterDto = new TheaterDto(null, "Theater 1", "Location 1", new ArrayList<>());
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");
        theater.setRooms(new ArrayList<>());

        when(theaterRepository.save(any(Theater.class))).thenReturn(theater);

        // When
        TheaterDto result = theaterService.createTheater(theaterDto);

        // Then
        assertNotNull(result);
        assertEquals("Theater 1", result.name());
        verify(theaterRepository, times(1)).save(any(Theater.class));
    }

    @Test
    void updateTheater() {
        // Given
        Theater existingTheater = new Theater();
        existingTheater.setId(1L);
        existingTheater.setName("Old Name");
        existingTheater.setLocation("Old Location");
        existingTheater.setRooms(new ArrayList<>());

        TheaterDto theaterDto = new TheaterDto(1L, "New Name", "New Location", List.of(1L));
        Room room = new Room();
        room.setId(1L);
        room.setName("Room 1");
        room.setCapacity(100);
        room.setAvailableSeats(50);
        room.setTheater(existingTheater);

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(existingTheater));
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(room));
        when(theaterRepository.save(any(Theater.class))).thenReturn(existingTheater);

        // When
        TheaterDto result = theaterService.updateTheater(1L, theaterDto);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.name());
        verify(theaterRepository, times(1)).save(any(Theater.class));
    }

    @Test
    void updateTheater_roomNotFound() {
        // Given
        Theater existingTheater = new Theater();
        existingTheater.setId(1L);
        existingTheater.setName("Old Name");
        existingTheater.setLocation("Old Location");
        existingTheater.setRooms(new ArrayList<>());

        TheaterDto theaterDto = new TheaterDto(1L, "New Name", "New Location", List.of(1L));

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(existingTheater));
        when(roomsRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> theaterService.updateTheater(1L, theaterDto));
        assertEquals("Room not found (theater service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deleteTheater() {
        // Given
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");
        theater.setRooms(new ArrayList<>());

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));

        // When
        theaterService.deleteTheater(1L);

        // Then
        verify(theaterRepository, times(1)).delete(theater);
    }

    @Test
    void deleteTheater_throwsException() {
        // Given
        when(theaterRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> theaterService.deleteTheater(1L));
        assertEquals("Theater not found (theater service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void addRoomToTheater() {
        // Given
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");
        theater.setRooms(new ArrayList<>());

        Room room = new Room();
        room.setId(1L);
        room.setName("Room 1");
        room.setCapacity(100);
        room.setAvailableSeats(50);
        room.setTheater(theater);

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(room));
        when(theaterRepository.save(any(Theater.class))).thenReturn(theater);

        // When
        TheaterDto result = theaterService.addRoomToTheater(1L, 1L);

        // Then
        assertNotNull(result);
        assertTrue(result.roomsIdx().contains(1L));
        verify(theaterRepository, times(1)).save(any(Theater.class));
    }

    @Test
    void addRoomToTheater_roomNotFound() {
        // Given
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");
        theater.setRooms(new ArrayList<>());

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(roomsRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> theaterService.addRoomToTheater(1L, 1L));
        assertEquals("Room not found (theater service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getRoomsInTheater() {
        // Given
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("Theater 1");
        theater.setLocation("Location 1");

        Room room = new Room();
        room.setId(1L);
        room.setName("Room 1");
        room.setCapacity(100);
        room.setAvailableSeats(50);
        room.setTheater(theater);

        theater.setRooms(List.of(room));

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));

        // When
        List<RoomDto> result = theaterService.getRoomsInTheater(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(theaterRepository, times(1)).findById(1L);
    }

    @Test
    void getRoomsInTheater_theaterNotFound() {
        // Given
        when(theaterRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> theaterService.getRoomsInTheater(1L));
        assertEquals("Theater not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
