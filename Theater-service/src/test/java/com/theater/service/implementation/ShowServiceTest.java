package com.theater.service.implementation;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;
import com.theater.entities.Movie;
import com.theater.entities.Room;
import com.theater.entities.Show;
import com.theater.entities.Theater;
import com.theater.exception.AppException;
import com.theater.repository.MovieRepository;
import com.theater.repository.RoomsRepository;
import com.theater.repository.SeatRepository;
import com.theater.repository.ShowRepository;
import com.theater.service.implementation.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowServiceTest {

    @InjectMocks
    private ShowService showService;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private SeatRepository seatRepository;

    private Show show;
    private ShowDto showDto;
    private Movie movie;
    private Room room;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        movie = new Movie(1L, "Movie Title", "Genre",120, "Description" );
        Theater theater = new Theater(); // Assuming Theater has a default constructor
        room = Room.builder()
                .id(1L)
                .theater(theater)
                .name("Room 1")
                .capacity(100)
                .availableSeats(100)  // Assuming no seats are taken initially
                .seats(new ArrayList<>()) // Initialize with an empty list of seats
                .shows(new ArrayList<>()) // Initialize with an empty list of shows
                .build();

        show = new Show(1L, movie, room, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2), 10.0);
        showDto = new ShowDto(1L, 1L, 1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2), 10.0);
    }

    @Test
    public void testGetAllShows() {
        List<Show> shows = List.of(show);
        when(showRepository.findAll()).thenReturn(shows);

        List<ShowDto> result = showService.getAllShows();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(showDto, result.get(0));
    }

    @Test
    public void testGetAllShowsEmpty() {
        when(showRepository.findAll()).thenReturn(new ArrayList<>());

        List<ShowDto> result = showService.getAllShows();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetShowByIdSuccess() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        ShowDto result = showService.getShowById(1L);
        assertNotNull(result);
        assertEquals(showDto, result);
    }

    @Test
    public void testGetShowByIdNotFound() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppException.class, () -> {
            showService.getShowById(1L);
        });

        assertEquals("Show not found (show service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((AppException) exception).getStatus());
    }

    @Test
    public void testCreateShowSuccess() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(room));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        ShowDto mockDto=new ShowDto(1L, 1L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), 10.0);
        ShowDto result = showService.createShow(mockDto);
        assertNotNull(result);
        assertEquals(showDto, result);
    }

    @Test
    public void testCreateShowStartTimeBeforeNow() {
        showDto = new ShowDto(1L, 1L, 1L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), 10.0);

        Exception exception = assertThrows(AppException.class, () -> {
            showService.createShow(showDto);
        });

        assertEquals("Start time of show cannot be before today", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, ((AppException) exception).getStatus());
    }

    @Test
    public void testUpdateShowSuccess() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(room));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        ShowDto result = showService.updateShow(1L, showDto);
        assertNotNull(result);
        assertEquals(showDto, result);
    }

    @Test
    public void testUpdateShowNotFound() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppException.class, () -> {
            showService.updateShow(1L, showDto);
        });

        assertEquals("Show not found (show service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((AppException) exception).getStatus());
    }

    @Test
    public void testDeleteShowSuccess() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        doNothing().when(showRepository).delete(any(Show.class));

        showService.deleteShow(1L);

        verify(showRepository, times(1)).delete(show);
    }

    @Test
    public void testDeleteShowFutureStartTime() {
        show = new Show(1L, movie, room, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), 10.0);
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));

        Exception exception = assertThrows(AppException.class, () -> {
            showService.deleteShow(1L);
        });

        assertEquals("Show cant be deleted", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, ((AppException) exception).getStatus());
    }

    @Test
    public void testAddMovieToShowSuccess() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        MovieDto result = showService.addMovieToShow(1L, 1L);
        assertNotNull(result);
        assertEquals(new MovieDto(movie.getId(), movie.getTitle(), movie.getDescription(), movie.getGenre(), movie.getDuration()), result);
    }

    @Test
    public void testAddMovieToShowNotFound() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppException.class, () -> {
            showService.addMovieToShow(1L, 1L);
        });

        assertEquals("Movie not found (show service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((AppException) exception).getStatus());
    }

    @Test
    public void testAddRoomToShowSuccess() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(roomsRepository.findById(1L)).thenReturn(Optional.of(room));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        ShowDto result = showService.addRoomToShow(1L, 1L);
        assertNotNull(result);
        assertEquals(showDto, result);
    }

    @Test
    public void testAddRoomToShowNotFound() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppException.class, () -> {
            showService.addRoomToShow(1L, 1L);
        });

        assertEquals("Show not found (show service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ((AppException) exception).getStatus());
    }
}
