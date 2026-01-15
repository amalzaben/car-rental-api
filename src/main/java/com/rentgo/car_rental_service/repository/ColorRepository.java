package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ColorRepository extends JpaRepository<Color, Long> {

    @Query("SELECT c FROM Color c WHERE c.name = :name")
    Optional<Color> findByName(@Param("name") String name);

    @Query("SELECT COUNT(c) > 0 FROM Color c WHERE c.name = :name")
    boolean existsByName(@Param("name") String name);
}
