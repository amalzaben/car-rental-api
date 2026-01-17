package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.CarColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarColorRepository extends JpaRepository<CarColor, Long> {

    @Query("""
    SELECT cc.color.name
    FROM CarColor cc
    WHERE cc.car.id = :carId
    """)
    List<String> findColorNamesByCarId(@Param("carId") Long carId);

    @Query("""
   SELECT cc
   FROM CarColor cc
   JOIN cc.color col
   WHERE cc.car.id = :carId
     AND LOWER(col.name) = LOWER(:colorName)
""")
    Optional<CarColor> findByCarIdAndColorName(Long carId, String colorName);
}
