package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Car;
import com.rentgo.car_rental_service.model.FavoriteCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteCarRepository extends JpaRepository<FavoriteCar, Long> {
    @Query("""
    SELECT (COUNT(fc) > 0)
    FROM FavoriteCar fc
    WHERE fc.user.id = :userId
      AND fc.car.id = :carId
    """)
    boolean existsByUserIdAndCarId(
            @Param("userId") Long userId,
            @Param("carId") Long carId
    );


    @Query("""
    SELECT fc
    FROM FavoriteCar fc
    WHERE fc.user.id = :userId
      AND fc.car.id = :carId
""")
    Optional<FavoriteCar> findByUserIdAndCarId(
            @Param("userId") Long userId,
            @Param("carId") Long carId
    );

    @Modifying
    @Query("""
        DELETE FROM FavoriteCar fc
        WHERE fc.user.id = :userId
          AND fc.car.id = :carId
    """)
    int deleteByUserIdAndCarId(@Param("userId") Long userId,
                               @Param("carId") Long carId);

    @Query("""
        SELECT fc.car
        FROM FavoriteCar fc
        WHERE fc.user.id = :userId
        ORDER BY fc.id DESC
    """)
    Page<Car> findFavoriteCarsByUserId(@Param("userId") Long userId, Pageable pageable);
}
