package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.FavoriteCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteCarRepository extends JpaRepository<FavoriteCar, Long> {
    boolean existsByUserIdAndCarId(Long userId, Long carId);

    Optional<FavoriteCar> findByUserIdAndCarId(Long userId, Long carId);
}
