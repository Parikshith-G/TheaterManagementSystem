package com.theater.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.theater.dtos.SeatStatusDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rooms")
@Builder
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;
    
    @Column(nullable = false)
    private int availableSeats=capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Show> shows = new ArrayList<>();


    public List<SeatStatusDto> getSeatStatuses() {
        List<SeatStatusDto> seatStatuses = new ArrayList<>();
        for (Seat seat : seats) {
            boolean isBooked = seat.isBooked(); // Implement this method in Seat entity
            seatStatuses.add(new SeatStatusDto(seat.getId(), isBooked));
        }
        return seatStatuses;
    }


    public Room(Theater theater, String name, int capacity, List<Show> shows) {
        this.theater = theater;
        this.name = name;
        this.capacity = capacity;
        this.shows = shows;
    }
}
