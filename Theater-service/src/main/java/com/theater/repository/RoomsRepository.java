package com.theater.repository;

import com.theater.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomsRepository extends JpaRepository<Room,Long> {
}
