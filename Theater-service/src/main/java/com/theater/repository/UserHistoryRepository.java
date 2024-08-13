package com.theater.repository;

import com.theater.entities.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory,Long> {
    boolean existsByUserId(Long userId);
    Optional<List<UserHistory>> findByUserId(Long userId);


}
