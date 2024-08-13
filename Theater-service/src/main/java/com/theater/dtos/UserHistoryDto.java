package com.theater.dtos;

import java.util.List;

public record UserHistoryDto(Long userId, List<String> history) {
}
