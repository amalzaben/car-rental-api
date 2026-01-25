package com.rentgo.car_rental_service.repository;

import com.rentgo.car_rental_service.model.PickUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickUpRepository extends JpaRepository<PickUp,Long> {
}
