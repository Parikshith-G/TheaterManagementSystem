package com.theater.service.implementation;

import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;
import com.theater.entities.Movie;
import com.theater.entities.Room;
import com.theater.entities.Show;
import com.theater.exception.AppException;
import com.theater.repository.MovieRepository;
import com.theater.repository.RoomsRepository;

import com.theater.repository.ShowRepository;
import com.theater.service.contract.IShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ShowService implements IShowService {

	private static final Logger logger = LoggerFactory.getLogger(ShowService.class);

	private final RoomsRepository roomsRepository;
	private final ShowRepository showRepository;
	
	private final MovieRepository movieRepository;

	@Autowired
	public ShowService(RoomsRepository roomsRepository, ShowRepository showRepository,
			MovieRepository movieRepository) {
		this.roomsRepository = roomsRepository;
		this.showRepository = showRepository;
	
		this.movieRepository = movieRepository;
	}

	@Override
	public List<ShowDto> getAllShows() {
		logger.info("Fetching all shows");
		List<Show> shows = showRepository.findAll();
		return shows.stream().map(this::showToShowDto).toList();
	}

	@Override
	public ShowDto getShowById(Long id) {
		logger.info("Fetching show with ID: {}", id);
		Show show = showRepository.findById(id)
				.orElseThrow(() -> new AppException("Show not found (show service)", HttpStatus.NOT_FOUND));
		return showToShowDto(show);
	}

	@Override
	public ShowDto createShow(ShowDto showDTO) {
		logger.info("Creating show");
		if(showDTO.startTime().isBefore(LocalDateTime.now())) {
			throw new AppException("Start time of show cannot be before today",HttpStatus.FORBIDDEN);
		}
		Show show = showDtoToShow(showDTO);
		Show savedShow = showRepository.save(show);
		logger.info("Created show with ID: {}", savedShow.getId());
		return showToShowDto(savedShow);
	}

	@Override
	public ShowDto updateShow(Long id, ShowDto showDTO) {
		logger.info("Updating show with ID: {}", id);
		Show show = showRepository.findById(id)
				.orElseThrow(() -> new AppException("Show not found (show service)", HttpStatus.NOT_FOUND));
		Movie movie = movieRepository.findById(showDTO.movieId())
				.orElseThrow(() -> new AppException("Movie not found (show service)", HttpStatus.NOT_FOUND));
		Room room = roomsRepository.findById(showDTO.roomId())
				.orElseThrow(() -> new AppException("Room not found (show service)", HttpStatus.NOT_FOUND));

		show.setMovie(movie);
		show.setRoom(room);
		show.setStartTime(showDTO.startTime());
		show.setEndTime(showDTO.endTime());
		Show savedShow = showRepository.save(show);
		logger.info("Updated show with ID: {}", savedShow.getId());
		return showToShowDto(savedShow);
	}

	@Override
    public void deleteShow(Long id) {

        logger.info("Deleting show with ID: {}", id);
        Show show = showRepository.findById(id).orElseThrow(() -> new AppException("Show not found (show service)", HttpStatus.NOT_FOUND));
      
		if (show.getStartTime().isAfter(LocalDateTime.now())) {
			throw new AppException("Show cant be deleted", HttpStatus.UNAUTHORIZED);
		}
        showRepository.delete(show);

        logger.info("Deleted show with ID: {}", id);
    }

	@Override
	public MovieDto addMovieToShow(Long showId, Long movieId) {
		logger.info("Adding movie with ID {} to show with ID {}", movieId, showId);
		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new AppException("Movie not found (show service)", HttpStatus.NOT_FOUND));
		Show show = showRepository.findById(showId)
				.orElseThrow(() -> new AppException("Show not found (show service)", HttpStatus.NOT_FOUND));
		show.setMovie(movie);
		showRepository.save(show);
		return movieToMovieDto(movie);
	}

	@Override
	public ShowDto addRoomToShow(Long showId, Long roomId) {
		logger.info("Adding room with ID {} to show with ID {}", roomId, showId);
		Show show = showRepository.findById(showId)
				.orElseThrow(() -> new AppException("Show not found (show service)", HttpStatus.NOT_FOUND));
		Room room = roomsRepository.findById(roomId)
				.orElseThrow(() -> new AppException("Room not found (show service)", HttpStatus.NOT_FOUND));
		List<Show> shows = new ArrayList<>();

		shows.add(show);
		room.setShows(shows);
		showRepository.save(show);
		return showToShowDto(show);
	}

	private ShowDto showToShowDto(Show show) {
		return new ShowDto(show.getId(), show.getMovie().getId(), show.getRoom().getId(), show.getStartTime(),
				show.getEndTime(), show.getPrice());
	}

	private Show showDtoToShow(ShowDto dto) {
		Movie movie = movieRepository.findById(dto.movieId())
				.orElseThrow(() -> new AppException("Movie not found (show service)", HttpStatus.NOT_FOUND));
		Room room = roomsRepository.findById(dto.roomId())
				.orElseThrow(() -> new AppException("Room not found (show service)", HttpStatus.NOT_FOUND));
		return Show.builder().id(dto.id()).movie(movie).room(room).startTime(dto.startTime()).endTime(dto.endTime())
				.price(dto.price()).build();
	}

	private MovieDto movieToMovieDto(Movie movie) {
		return new MovieDto(movie.getId(), movie.getTitle(), movie.getDescription(), movie.getGenre(),

				movie.getDuration());
	}
}