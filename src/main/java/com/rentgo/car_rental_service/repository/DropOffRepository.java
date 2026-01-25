package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.DropOff;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DropOffRepository extends JpaRepository< DropOff, Long> {
    @Query("""
    SELECT d
    FROM DropOff d
    WHERE d.isAssigned = false
      AND d.date >= :start
      AND d.date < :end
""")
    List<DropOff> findTodayUnassignedDropoffs(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DropOff d WHERE d.id = :id")
    Optional<DropOff> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    SELECT d
    FROM DropOff d
    WHERE d.isAssigned = true
      AND d.assignedToEmployeeId = :employeeId
    ORDER BY d.date DESC
""")
    List<DropOff> findAssignedToEmployee(@Param("employeeId") Long employeeId);

}
