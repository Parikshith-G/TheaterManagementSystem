package com.theater.service.implementation;

import com.theater.dtos.MovieDto;
import com.theater.entities.Movie;
import com.theater.exception.AppException;
import com.theater.repository.MovieRepository;

import com.theater.service.contract.IMovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class MovieService implements IMovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    
    }

    @Override
    public List<MovieDto> getAllMovies() {
        logger.info("Fetching all movies");
        List<Movie> movies = movieRepository.findAll();
        logger.info("Fetched {} movies", movies.size());
        return movies.stream().map(this::movieToMovieDto).toList();
    }

    @Override
    public MovieDto getMovieById(Long id) {
        logger.info("Fetching movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException("movie not found(movie service)", HttpStatus.NOT_FOUND));
        logger.info("Fetched movie: {}", movie.getTitle());
        return movieToMovieDto(movie);
    }

    @Override
    public MovieDto createMovie(MovieDto movieDTO) {
        logger.info("Creating movie with title: {}", movieDTO.title());
        Movie movie = movieRepository.save(movieDtoToMovie(movieDTO));
        logger.info("Created movie with ID: {}", movie.getId());
        return movieToMovieDto(movie);
    }

    @Override
    public MovieDto updateMovie(Long id, MovieDto movieDTO) {
        logger.info("Updating movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException("movie not found(movie service)", HttpStatus.NOT_FOUND));
        movie.setTitle(movieDTO.title());
        movie.setGenre(movieDTO.genre());
        movie.setDuration(movieDTO.duration());
        movie.setDescription(movieDTO.description());
        
        Movie savedMovie = movieRepository.save(movie);
        logger.info("Updated movie with ID: {}", savedMovie.getId());
        return movieToMovieDto(savedMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        logger.info("Deleting movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException("movie not found(movie service)", HttpStatus.NOT_FOUND));
        movieRepository.delete(movie);
        logger.info("Deleted movie with ID: {}", id);
    }

    private MovieDto movieToMovieDto(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getGenre(),
                
                movie.getDuration());
    }

    private Movie movieDtoToMovie(MovieDto dto) {
        return Movie.builder()
                .id(dto.id())
                .title(dto.title())
                .genre(dto.genre())
                .duration(dto.duration())
                .description(dto.description())
                
                .build();
    }
}
