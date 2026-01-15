package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.ENUM.UserRole;
import com.rentgo.car_rental_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.role = :role")
    boolean existsByIdAndRole(@Param("id") Long id, @Param("role") UserRole role);
}
