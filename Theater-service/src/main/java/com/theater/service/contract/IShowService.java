package com.theater.service.contract;

import com.theater.dtos.MovieDto;
import com.theater.dtos.ShowDto;

import java.util.List;

public interface IShowService {

    List<ShowDto> getAllShows();

    ShowDto getShowById(Long id);

    ShowDto createShow(ShowDto showDTO);

    ShowDto updateShow(Long id, ShowDto showDTO);

    void deleteShow(Long id);

     MovieDto addMovieToShow(Long showId, Long movieId);

    ShowDto addRoomToShow(Long showId, Long roomId);
}
