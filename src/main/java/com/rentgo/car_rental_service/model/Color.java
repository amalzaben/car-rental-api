package com.rentgo.car_rental_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", nullable=false, length=25, unique=true)
    private String name;
}