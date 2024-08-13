package com.theater.dtos;

import java.util.List;

public record UserPreferenceDto(Long userId, List<String> preferences){
}
