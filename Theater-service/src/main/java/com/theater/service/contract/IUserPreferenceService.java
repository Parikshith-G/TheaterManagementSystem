package com.theater.service.contract;

import com.theater.dtos.UserPreferenceDto;

public interface IUserPreferenceService {
    UserPreferenceDto getUserPreferences(Long userId);

    UserPreferenceDto addUserPreferences(Long userId, String preference);

    public boolean deleteUserPreferences(Long userId, Long preferenceId);
}
