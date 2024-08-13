package com.theater.service.implementation;

import com.theater.dtos.UserHistoryDto;
import com.theater.entities.UserHistory;
import com.theater.exception.AppException;
import com.theater.repository.UserHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHistoryServiceTest {

    @Mock
    private UserHistoryRepository repo;

    @InjectMocks
    private UserHistoryService userHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserHistory() {
        // Given
        Long userId = 1L;
        UserHistory history1 = UserHistory.builder().userId(userId).historyItem("Item 1").build();
        UserHistory history2 = UserHistory.builder().userId(userId).historyItem("Item 2").build();
        List<UserHistory> historyList = List.of(history1, history2);

        when(repo.findByUserId(userId)).thenReturn(Optional.of(historyList));

        // When
        UserHistoryDto result = userHistoryService.getUserHistory(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(2, result.history().size());
        assertTrue(result.history().contains("Item 1"));
        assertTrue(result.history().contains("Item 2"));
        verify(repo, times(1)).findByUserId(userId);
    }

    @Test
    void getUserHistory_userNotFound() {
        // Given
        Long userId = 1L;
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> userHistoryService.getUserHistory(userId));
        assertEquals("User not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repo, times(1)).findByUserId(userId);
    }


    @Test
    void putUserHistory() {
        // Given
        Long userId = 1L;
        String historyItem = "New Item";
        List<UserHistory> existingHistories = List.of(); // Simulate an empty history list

        // Mocking repository behavior
        when(repo.existsByUserId(userId)).thenReturn(true); // Simulate that the user exists
        when(repo.findByUserId(userId)).thenReturn(Optional.of(existingHistories));
        // Simulate saving the new history item
        UserHistory savedHistory = UserHistory.builder()
                .userId(userId)
                .historyItem(historyItem)
                .build();
        when(repo.save(any(UserHistory.class))).thenReturn(savedHistory);
        // Simulate retrieval of updated history
        when(repo.findByUserId(userId)).thenReturn(Optional.of(List.of(savedHistory)));

        // When
        UserHistoryDto result = userHistoryService.putUserHistory(userId, historyItem);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertTrue(result.history().contains(historyItem), "The history item should be present in the result");

        // Verify interactions
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, times(1)).save(any(UserHistory.class));
        verify(repo, times(1)).findByUserId(userId);
    }


    @Test
    void putUserHistory_userNotFound() {
        // Given
        Long userId = 1L;
        String historyItem = "New Item";

        when(repo.existsByUserId(userId)).thenReturn(false);

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> userHistoryService.putUserHistory(userId, historyItem));
        assertEquals("User doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, never()).save(any(UserHistory.class));
    }

    @Test
    void deleteUserHistory() {
        // Given
        Long userId = 1L;
        Long historyId = 1L;
        UserHistory history = UserHistory.builder().userId(userId).id(historyId).build();

        when(repo.existsByUserId(userId)).thenReturn(true);
        when(repo.existsById(historyId)).thenReturn(true);
        when(repo.findById(historyId)).thenReturn(Optional.of(history));

        // When
        boolean result = userHistoryService.deleteUserHistory(userId, historyId);

        // Then
        assertTrue(result);
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, times(1)).existsById(historyId);
        verify(repo, times(1)).findById(historyId);
        verify(repo, times(1)).delete(history);
    }

    @Test
    void deleteUserHistory_userNotFound() {
        // Given
        Long userId = 1L;
        Long historyId = 1L;

        when(repo.existsByUserId(userId)).thenReturn(false);

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> userHistoryService.deleteUserHistory(userId, historyId));
        assertEquals("User doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, never()).existsById(historyId);
        verify(repo, never()).findById(historyId);
        verify(repo, never()).delete(any(UserHistory.class));
    }

    @Test
    void deleteUserHistory_historyNotFound() {
        // Given
        Long userId = 1L;
        Long historyId = 1L;

        when(repo.existsByUserId(userId)).thenReturn(true);
        when(repo.existsById(historyId)).thenReturn(false);

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> userHistoryService.deleteUserHistory(userId, historyId));
        assertEquals("History doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, times(1)).existsById(historyId);
        verify(repo, never()).findById(historyId);
        verify(repo, never()).delete(any(UserHistory.class));
    }

    @Test
    void deleteUserHistory_historyDoesNotBelongToUser() {
        // Given
        Long userId = 1L;
        Long historyId = 1L;
        UserHistory history = UserHistory.builder().userId(2L).id(historyId).build();

        when(repo.existsByUserId(userId)).thenReturn(true);
        when(repo.existsById(historyId)).thenReturn(true);
        when(repo.findById(historyId)).thenReturn(Optional.of(history));

        // When / Then
        AppException exception = assertThrows(AppException.class, () -> userHistoryService.deleteUserHistory(userId, historyId));
        assertEquals("History doesn't belong to user", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(repo, times(1)).existsByUserId(userId);
        verify(repo, times(1)).existsById(historyId);
        verify(repo, times(1)).findById(historyId);
        verify(repo, never()).delete(any(UserHistory.class));
    }
}
