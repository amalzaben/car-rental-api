package com.rentgo.car_rental_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "car_color",
        uniqueConstraints = @UniqueConstraint(name = "uq_car_color", columnNames = {"car_id", "color_id"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(name = "available", nullable = false)
    private boolean available = true;
}