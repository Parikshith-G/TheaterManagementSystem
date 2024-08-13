package com.theater.repository;

import com.theater.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference,Long> {
    Optional<List<UserPreference>> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
