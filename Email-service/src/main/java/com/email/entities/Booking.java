package com.email.entities;

import java.time.LocalDateTime;

public record Booking(Long id, String userMail, Long showId, LocalDateTime bookingDate, LocalDateTime showTime,
		String seatId, String bookingReference, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
		String userName) {

}
