package com.theater.service.implementation;

import com.theater.dtos.UserPreferenceDto;
import com.theater.entities.UserPreference;
import com.theater.exception.AppException;
import com.theater.repository.UserPreferenceRepository;
import com.theater.service.contract.IUserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService implements IUserPreferenceService {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferenceService.class);

    private final UserPreferenceRepository repo;

    @Override
    public UserPreferenceDto getUserPreferences(Long userId) {
        logger.info("Fetching user preferences for user with ID: {}", userId);
        Optional<List<UserPreference>> preferencesOpt = repo.findByUserId(userId);
        if (preferencesOpt.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        List<String> preferencesList = preferencesOpt.get()
                .stream()
                .map(UserPreference::getPreference)
                .toList();

        return new UserPreferenceDto(userId, preferencesList);
    }

    @Override
    public UserPreferenceDto addUserPreferences(Long userId, String preference) {
        logger.info("Adding preference '{}' for user with ID: {}", preference, userId);
        boolean exists = repo.existsByUserId(userId);
        if (!exists) {
            throw new AppException("User doesn't exist", HttpStatus.NOT_FOUND);
        }

        UserPreference userPreference = UserPreference.builder()
                .userId(userId)
                .preference(preference)
                .build();

        repo.save(userPreference);
        return userPreferenceToUserPreferenceDto(userId);
    }

    @Override
    public boolean deleteUserPreferences(Long userId, Long preferenceId) {
        logger.info("Deleting preference with ID {} for user with ID {}", preferenceId, userId);
        boolean userExists = repo.existsByUserId(userId);
        if (!userExists) {
            throw new AppException("User doesn't exist", HttpStatus.NOT_FOUND);
        }

        boolean preferenceExists = repo.existsById(preferenceId);
        if (!preferenceExists) {
            throw new AppException("Preference doesn't exist", HttpStatus.NOT_FOUND);
        }

        repo.deleteById(preferenceId);
        return true;
    }

    private UserPreferenceDto userPreferenceToUserPreferenceDto(Long userId) {
        List<UserPreference> preferences = repo.findByUserId(userId).get();
        List<String> _preferences = preferences.stream().map(UserPreference::getPreference).toList();
        return new UserPreferenceDto(userId, _preferences);
    }
}
