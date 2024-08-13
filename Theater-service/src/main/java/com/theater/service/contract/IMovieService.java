package com.theater.service.contract;

import com.theater.dtos.MovieDto;

import java.util.List;

public interface IMovieService {

    List<MovieDto> getAllMovies();

    MovieDto getMovieById(Long id);

    MovieDto createMovie(MovieDto movieDTO);

    MovieDto updateMovie(Long id, MovieDto movieDTO);

    void deleteMovie(Long id);


}
