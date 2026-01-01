package com.rentgo.car_rental_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "favorite_cars",
        uniqueConstraints = @UniqueConstraint(name = "uq_favorite_cars", columnNames = {"user_id", "car_id"})
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
}
