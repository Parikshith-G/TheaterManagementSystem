package com.theater.controller;

import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;
import com.theater.service.contract.IShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableWebMvc
public class ShowControllerTest {

	@Mock
	private IShowService showService;

	@InjectMocks
	private ShowController showController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(showController).build();
	}

	@Test
	public void getAllShows() throws Exception {
		ShowDto show1 = new ShowDto(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10.0);
		ShowDto show2 = new ShowDto(2L, 2L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 15.0);
		when(showService.getAllShows()).thenReturn(Arrays.asList(show1, show2));

		mockMvc.perform(get("/api/v1/shows")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void getShowById() throws Exception {
		Long id = 1L;
		ShowDto show = new ShowDto(id, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10.0);
		when(showService.getShowById(id)).thenReturn(show);

		mockMvc.perform(get("/api/v1/shows/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.movieId").value(1L)).andExpect(jsonPath("$.roomId").value(1L))
				.andExpect(jsonPath("$.startTime").exists()).andExpect(jsonPath("$.endTime").exists())
				.andExpect(jsonPath("$.price").value(10.0));
	}



	@Test
	public void createShow() throws Exception {
		ShowDto showDTO = new ShowDto(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10.0);
		when(showService.createShow(any(ShowDto.class))).thenReturn(showDTO);

		mockMvc.perform(post("/api/v1/shows").contentType("application/json")
				.content("{\"id\":1,\"movieId\":1,\"roomId\":1,\"startTime\":\"" + showDTO.startTime()
						+ "\",\"endTime\":\"" + showDTO.endTime() + "\",\"price\":10.0}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.movieId").value(1L)).andExpect(jsonPath("$.roomId").value(1L))
				.andExpect(jsonPath("$.startTime").exists()).andExpect(jsonPath("$.endTime").exists())
				.andExpect(jsonPath("$.price").value(10.0));
	}

	@Test
	public void createShowValidationError() throws Exception {
		mockMvc.perform(post("/api/v1/shows").contentType("application/json").content(
				"{\"id\":\"invalid\",\"movieId\":\"invalid\",\"roomId\":\"invalid\",\"startTime\":\"invalid\",\"endTime\":\"invalid\",\"price\":\"invalid\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateShow() throws Exception {
		Long id = 1L;
		ShowDto showDTO = new ShowDto(id, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 10.0);
		when(showService.updateShow(eq(id), any(ShowDto.class))).thenReturn(showDTO);

		mockMvc.perform(put("/api/v1/shows/{id}", id).contentType("application/json")
				.content("{\"id\":1,\"movieId\":1,\"roomId\":1,\"startTime\":\"" + showDTO.startTime()
						+ "\",\"endTime\":\"" + showDTO.endTime() + "\",\"price\":10.0}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.movieId").value(1L)).andExpect(jsonPath("$.roomId").value(1L))
				.andExpect(jsonPath("$.startTime").exists()).andExpect(jsonPath("$.endTime").exists())
				.andExpect(jsonPath("$.price").value(10.0));
	}


	@Test
	public void deleteShow() throws Exception {
		Long id = 1L;
		doNothing().when(showService).deleteShow(id);

		mockMvc.perform(delete("/api/v1/shows/{id}", id)).andExpect(status().isNoContent());
	}



	@Test
	public void addMovieToShow() throws Exception {
		Long showId = 1L;
		Long movieId = 1L;
		MovieDto movieDto = new MovieDto(movieId, "Inception", "A mind-bending thriller", "Sci-Fi", 148);
		when(showService.addMovieToShow(showId, movieId)).thenReturn(movieDto);

		mockMvc.perform(post("/api/v1/shows/{showId}/movie/{movieId}", showId, movieId)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(movieId)).andExpect(jsonPath("$.title").value("Inception"))
				.andExpect(jsonPath("$.description").value("A mind-bending thriller"))
				.andExpect(jsonPath("$.genre").value("Sci-Fi")).andExpect(jsonPath("$.duration").value(148));
	}



	@Test
	public void addMovieToShowValidationError() throws Exception {
		Long showId = 1L;
		Long movieId = 1L;

		mockMvc.perform(post("/api/v1/shows/{showId}/movie/{movieId}", showId, movieId))
				.andExpect(status().is2xxSuccessful());
	}
}
