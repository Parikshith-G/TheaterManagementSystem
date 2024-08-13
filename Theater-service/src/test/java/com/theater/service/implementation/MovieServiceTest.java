
package com.theater.service.implementation;

import com.theater.dtos.MovieDto;
import com.theater.entities.Movie;
import com.theater.exception.AppException;
import com.theater.repository.MovieRepository;
import com.theater.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMovies() {
        Movie movie1 = Movie.builder()
                .id(1L)
                .title("Title1")
                .description("Description1")
                .genre("Genre1")
                .duration(120)
                .build();
        Movie movie2 = Movie.builder()
                .id(2L)
                .title("Title2")
                .description("Description2")
                .genre("Genre2")
                .duration(150)
                .build();
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie1, movie2));

        List<MovieDto> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertEquals("Title1", result.get(0).title());
        assertEquals("Title2", result.get(1).title());
    }

    @Test
    void getMovieById_success() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .genre("Genre")
                .duration(120)
                .build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieDto result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals("Title", result.title());
    }

    @Test
    void getMovieById_notFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> movieService.getMovieById(1L));

        assertEquals("movie not found(movie service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void createMovie() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .genre("Genre")
                .duration(120)
                .build();
        MovieDto movieDto = new MovieDto(1L, "Title", "Description", "Genre", 120);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDto result = movieService.createMovie(movieDto);

        assertNotNull(result);
        assertEquals("Title", result.title());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void updateMovie_success() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .genre("Old Genre")
                .duration(120)
                .build();
        MovieDto movieDto = new MovieDto(1L, "New Title", "New Description", "New Genre", 150);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDto result = movieService.updateMovie(1L, movieDto);

        assertNotNull(result);
        assertEquals("New Title", result.title());
        assertEquals(150, result.duration());
    }

    @Test
    void updateMovie_notFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        MovieDto movieDto = new MovieDto(1L, "Title", "Description", "Genre", 120);

        AppException exception = assertThrows(AppException.class, () -> movieService.updateMovie(1L, movieDto));

        assertEquals("movie not found(movie service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void deleteMovie_success() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Title")
                .description("Description")
                .genre("Genre")
                .duration(120)
                .build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).delete(movie);
    }

    @Test
    void deleteMovie_notFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> movieService.deleteMovie(1L));

        assertEquals("movie not found(movie service)", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
