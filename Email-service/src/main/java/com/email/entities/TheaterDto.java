package com.email.entities;

import java.util.List;

public record TheaterDto(Long id, String name, String location, List<Long> roomsIdx) {

}
