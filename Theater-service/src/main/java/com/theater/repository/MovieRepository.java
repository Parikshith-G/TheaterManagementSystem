package com.theater.repository;

import com.theater.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MovieRepository  extends JpaRepository<Movie,Long> {
    Optional<Object> findByTitle(String s);
}
