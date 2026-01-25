package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Car;
import com.rentgo.car_rental_service.model.ENUM.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.id = :id")
    Optional<Car> findById(@Param("id") Long id);

    @Query(
            value = """
        SELECT DISTINCT c
        FROM Car c
        LEFT JOIN CarColor cc ON cc.car.id = c.id
        LEFT JOIN Color col ON col.id = cc.color.id
        WHERE (:status IS NULL OR c.status = :status)
          AND (:type IS NULL OR LOWER(c.type) = LOWER(:type))
          AND (:fuel IS NULL OR LOWER(c.fuel) = LOWER(:fuel))
          AND (:seats IS NULL OR c.seats = :seats)
          AND (:minPrice IS NULL OR c.price >= :minPrice)
          AND (:maxPrice IS NULL OR c.price <= :maxPrice)
          AND (:color IS NULL OR LOWER(col.name) = LOWER(:color))
          AND (
            :q IS NULL OR :q = '' OR
            LOWER(c.family) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(c.type)   LIKE LOWER(CONCAT('%', :q, '%'))
          )
      """,
            countQuery = """
        SELECT COUNT(DISTINCT c.id)
        FROM Car c
        LEFT JOIN CarColor cc ON cc.car.id = c.id
        LEFT JOIN Color col ON col.id = cc.color.id
        WHERE (:status IS NULL OR c.status = :status)
          AND (:type IS NULL OR LOWER(c.type) = LOWER(:type))
          AND (:fuel IS NULL OR LOWER(c.fuel) = LOWER(:fuel))
          AND (:seats IS NULL OR c.seats = :seats)
          AND (:minPrice IS NULL OR c.price >= :minPrice)
          AND (:maxPrice IS NULL OR c.price <= :maxPrice)
          AND (:color IS NULL OR LOWER(col.name) = LOWER(:color))
          AND (
            :q IS NULL OR :q = '' OR
            LOWER(c.family) LIKE LOWER(CONCAT('%', :q, '%')) OR
            LOWER(c.type)   LIKE LOWER(CONCAT('%', :q, '%'))
          )
      """
    )
    Page<Car> searchCars(
            @Param("q") String q,
            @Param("type") String type,
            @Param("color") String color,
            @Param("seats") Integer seats,
            @Param("fuel") String fuel,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") CarStatus status,
            Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(c.id), 0) FROM Car c")
    Long findMaxId();
}
