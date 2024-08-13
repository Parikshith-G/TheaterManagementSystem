package com.theater.service.implementation;

import com.theater.dtos.UserHistoryDto;
import com.theater.entities.UserHistory;
import com.theater.exception.AppException;
import com.theater.repository.UserHistoryRepository;
import com.theater.service.contract.IUserHistoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserHistoryService implements IUserHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(UserHistoryService.class);

    private final UserHistoryRepository repo;

    @Override
    public UserHistoryDto getUserHistory(Long userId) {
        logger.info("Fetching user history for user with ID: {}", userId);
        List<UserHistory> _history = repo.findByUserId(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        List<String> history = _history.stream()
                .map(UserHistory::getHistoryItem)
                .toList();

        return new UserHistoryDto(userId, history);
    }

    @Override
    public UserHistoryDto putUserHistory(Long userId, String historyItem) {
        logger.info("Adding history item for user with ID: {}", userId);
        boolean exists = repo.existsByUserId(userId);
        if (!exists) {
            throw new AppException("User doesn't exist", HttpStatus.NOT_FOUND);
        }

        UserHistory history = UserHistory.builder()
                .userId(userId)
                .historyItem(historyItem)
                .build();

        repo.save(history);
        return userToUserHistoryDto(userId);
    }

    private UserHistoryDto userToUserHistoryDto(Long userId) {
        List<UserHistory> histories = repo.findByUserId(userId).get();
        List<String> history = histories.stream()
                .map(UserHistory::getHistoryItem)
                .toList();

        return new UserHistoryDto(userId, history);
    }

    @Override
    public boolean deleteUserHistory(Long userId, Long historyId) {
        logger.info("Deleting history item with ID {} for user with ID {}", historyId, userId);
        boolean userExists = repo.existsByUserId(userId);
        if (!userExists) {
            throw new AppException("User doesn't exist", HttpStatus.NOT_FOUND);
        }

        boolean historyExists = repo.existsById(historyId);
        if (!historyExists) {
            throw new AppException("History doesn't exist", HttpStatus.NOT_FOUND);
        }

        UserHistory historyObj = repo.findById(historyId).get();
        if (!Objects.equals(historyObj.getUserId(), userId)) {
            throw new AppException("History doesn't belong to user", HttpStatus.NOT_FOUND);
        }

        repo.delete(historyObj);
        return true;
    }
}
