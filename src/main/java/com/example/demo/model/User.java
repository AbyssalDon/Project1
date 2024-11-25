package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "pdf")
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID userId;
    private Integer cpr;
    private String name;
    private LocalDateTime createdAt;
    private String pdfname;
}
