package com.theater.booking.entity;

import lombok.Data;

@Data
public class Seat {

	private Long id;

	private String seatNumber;

	private boolean booked;
}
