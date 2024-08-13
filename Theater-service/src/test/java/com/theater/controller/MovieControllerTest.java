package com.theater.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theater.dtos.MovieDto;
import com.theater.service.contract.IMovieService;
import com.theater.service.contract.IShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMovieService movieService;

    @MockBean
    private IShowService showService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDto movieDto;
    private Long movieId = 1L;

    @BeforeEach
    public void setUp() {
        movieDto = new MovieDto(movieId, "Movie Title", "Movie Description", "Genre", 120);
    }

    @Test
    public void testGetAllMovies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movieDto));

        mockMvc.perform(get("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(movieId))
                .andExpect(jsonPath("$[0].title").value("Movie Title"))
                .andExpect(jsonPath("$[0].description").value("Movie Description"))
                .andExpect(jsonPath("$[0].genre").value("Genre"))
                .andExpect(jsonPath("$[0].duration").value(120));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    public void testGetMovieById() throws Exception {
        when(movieService.getMovieById(movieId)).thenReturn(movieDto);

        mockMvc.perform(get("/api/v1/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Movie Title"))
                .andExpect(jsonPath("$.description").value("Movie Description"))
                .andExpect(jsonPath("$.genre").value("Genre"))
                .andExpect(jsonPath("$.duration").value(120));

        verify(movieService, times(1)).getMovieById(movieId);
    }

    @Test
    public void testCreateMovie() throws Exception {
        when(movieService.createMovie(any(MovieDto.class))).thenReturn(movieDto);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Movie Title"))
                .andExpect(jsonPath("$.description").value("Movie Description"))
                .andExpect(jsonPath("$.genre").value("Genre"))
                .andExpect(jsonPath("$.duration").value(120));

        verify(movieService, times(1)).createMovie(any(MovieDto.class));
    }

    @Test
    public void testUpdateMovie() throws Exception {
        when(movieService.updateMovie(eq(movieId), any(MovieDto.class))).thenReturn(movieDto);

        mockMvc.perform(put("/api/v1/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Movie Title"))
                .andExpect(jsonPath("$.description").value("Movie Description"))
                .andExpect(jsonPath("$.genre").value("Genre"))
                .andExpect(jsonPath("$.duration").value(120));

        verify(movieService, times(1)).updateMovie(eq(movieId), any(MovieDto.class));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        doNothing().when(movieService).deleteMovie(movieId);

        mockMvc.perform(delete("/api/v1/movies/{id}", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).deleteMovie(movieId);
    }
}
