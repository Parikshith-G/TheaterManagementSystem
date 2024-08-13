package com.email.entities;

import java.time.LocalDateTime;

public record EmailWrapper(String theaterName, String theaterLocation, String roomName, String theaterContact,
		LocalDateTime startTime, LocalDateTime endTime, String userName, String movieName, String seatName,
		String bookingReference, String userMail, Double moviePrice) {

}
