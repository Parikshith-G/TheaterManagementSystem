package com.login.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "files")
@Data
public class FileDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    private byte[] data;

    // Constructors, getters, and setters
}
