package com.theater.dtos;


public record MovieDto(
        Long id,
        String title,
        String description,
        String genre,  
        int duration
) {
}
