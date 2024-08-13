package com.theater.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "movie")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "genre",nullable = false)
    private String genre;

    @Column(name = "duration",nullable = false)
    private int duration;

    @Column(name = "description",nullable = false)
    private String description;

    public Movie(String title, String genre, int duration, String description) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.description = description;
       
    }
}
