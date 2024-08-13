package com.theater.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "theater")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Theater {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "name")
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(name = "location",nullable = false)
    @NotBlank(message = "Location is mandatory")
    private String location;


    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Room> rooms;
}
