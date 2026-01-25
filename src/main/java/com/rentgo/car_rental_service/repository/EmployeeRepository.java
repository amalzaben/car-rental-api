package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Query("SELECT e FROM Employee e WHERE e.user.id = :userId")
    Optional<Employee> findByUserId(@Param("userId") Long userId);
    

    @Query("""
    SELECT EXISTS (
        SELECT 1
        FROM Employee e
        WHERE LOWER(e.email) = LOWER(:email)
          AND e.id <> :id
    )
""")
    Boolean existsByEmailIgnoreCaseAndIdNot(
            @Param("email") String email,
            @Param("id") Long id
    );

}
