package com.theater.service.contract;

import com.theater.dtos.UserHistoryDto;

public interface IUserHistoryService {
    UserHistoryDto getUserHistory(Long userId);

    UserHistoryDto putUserHistory(Long userId,String historyItem);

    boolean deleteUserHistory(Long userId, Long historyId);
}
