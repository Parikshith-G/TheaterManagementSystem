package com.theater.repository;

import com.theater.entities.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TheaterRepository extends JpaRepository<Theater,Long> {
    Optional<Object> findByName(String s);
}
