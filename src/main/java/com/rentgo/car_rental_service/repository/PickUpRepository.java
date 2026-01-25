package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.PickUp;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickUpRepository extends JpaRepository<PickUp,Long> {

    @Query("""
    SELECT p
    FROM PickUp p
    WHERE p.isAssigned = false
      AND p.date >= :start
      AND p.date < :end
""")
    List<PickUp> findTodayUnassignedPickups(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PickUp p WHERE p.id = :id")
    Optional<PickUp> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    SELECT p
    FROM PickUp p
    WHERE p.isAssigned = true
      AND p.assignedToEmployeeId = :employeeId
    ORDER BY p.date DESC
""")
    List<PickUp> findAssignedToEmployee(@Param("employeeId") Long employeeId);


}
